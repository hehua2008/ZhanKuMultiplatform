package com.hym.zhankucompose.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.hym.zhankucompose.MyAppViewModel
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.compose.LabelFlowLayout
import com.hym.zhankucompose.compose.SMALL_PADDING_VALUES
import com.hym.zhankucompose.compose.SimpleRadioGroup
import com.hym.zhankucompose.getAppViewModel
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.PagedLayout
import com.hym.zhankucompose.ui.TabReselectedCallback
import com.hym.zhankucompose.ui.main.MainViewModel
import com.hym.zhankucompose.ui.main.PreviewLayout
import com.hym.zhankucompose.ui.theme.ComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList

@AndroidEntryPoint
class SearchContentFragment : Fragment(), Observer<LifecycleOwner>, TabReselectedCallback {
    companion object {
        private const val TAG = "SearchContentFragment"
        const val CONTENT_TYPE = "CONTENT_TYPE"

        @JvmStatic
        fun newInstance(contentType: ContentType): SearchContentFragment {
            val fragment = SearchContentFragment()
            fragment.arguments = Bundle().apply {
                putString(CONTENT_TYPE, contentType.name)
            }
            return fragment
        }
    }

    private val mMainViewModel: MainViewModel by activityViewModels()
    private val mPageViewModel: SearchContentPageViewModel by viewModels()
    private lateinit var mContentType: ContentType

    private var scrollToTop: () -> Unit = EMPTY_BLOCK

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(CONTENT_TYPE, mContentType.name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwnerLiveData.observe(this, this)

        val activeBundle = savedInstanceState ?: arguments
        mContentType = ContentType.valueOf(activeBundle!!.getString(CONTENT_TYPE)!!)
        mPageViewModel.contentType = mContentType
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ComposeTheme {
                    val lazyPagingItems = mPageViewModel.pagingFlow.collectAsLazyPagingItems()
                    val lifecycleOwner = LocalLifecycleOwner.current

                    DisposableEffect(lazyPagingItems, lifecycleOwner) {
                        val observer = Observer<Unit> { lazyPagingItems.refresh() }
                        mPageViewModel.mediatorLiveData.observe(lifecycleOwner, observer)

                        onDispose {
                            mPageViewModel.mediatorLiveData.removeObserver(observer)
                        }
                    }

                    val topCates by getAppViewModel<MyAppViewModel>().categoryItems.observeAsState(
                        persistentListOf()
                    )
                    val topCateList = remember(topCates) {
                        mutableListOf(TopCate.All).apply {
                            addAll(topCates)
                        }.toImmutableList()
                    }
                    val topCateListNameList = remember(topCateList) {
                        topCateList.map { it.name }.toImmutableList()
                    }
                    val topCate by mPageViewModel.topCate.observeAsState(TopCate.All)
                    val defaultCategoryIndex = remember(topCateList, topCate) {
                        topCateList.indexOf(topCate).let { if (it < 0) 0 else it }
                    }

                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                    val labelTextStyle = MaterialTheme.typography.labelLarge.let {
                        remember(it, onSurfaceColor) { it.copy(color = onSurfaceColor) }
                    }

                    val recommendLevels = remember {
                        listOf(
                            RecommendLevel.ALL_LEVEL,
                            RecommendLevel.EDITOR_CHOICE,
                            RecommendLevel.ALL_RECOMMEND,
                            RecommendLevel.HOME_RECOMMEND
                        ).toImmutableList()
                    }

                    val sortOrders = remember {
                        listOf(
                            SortOrder.BEST_MATCH,
                            SortOrder.LATEST_PUBLISH,
                            SortOrder.MOST_RECOMMEND,
                            SortOrder.MOST_COMMENT
                        ).toImmutableList()
                    }

                    val activePage by mPageViewModel.page.observeAsState(1)
                    val lastPage by mPageViewModel.totalPages.observeAsState(1)
                    val pageSizeIndex by mPageViewModel.pageSizeIndex.observeAsState(0)

                    PreviewLayout(
                        lazyPagingItems = lazyPagingItems,
                        setOnScrollToTopAction = { scrollToTop = it }
                    ) { headerModifier ->
                        Column(
                            modifier = headerModifier,
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            LabelFlowLayout(
                                labels = topCateListNameList,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = COMMON_PADDING),
                                defaultLabelIndex = defaultCategoryIndex,
                                itemPadding = SMALL_PADDING_VALUES
                            ) {
                                mPageViewModel.setTopCate(topCateList[it])
                            }

                            SimpleRadioGroup(
                                items = recommendLevels,
                                modifier = Modifier.padding(top = COMMON_PADDING),
                                onItemSelected = { level ->
                                    mPageViewModel.setRecommendLevel(level)
                                }
                            ) { level ->
                                Text(
                                    text = level.text,
                                    maxLines = 1,
                                    style = labelTextStyle
                                )
                            }

                            SimpleRadioGroup(
                                items = sortOrders,
                                modifier = Modifier.padding(top = COMMON_PADDING),
                                onItemSelected = { order ->
                                    mPageViewModel.setSortOrder(order)
                                }
                            ) { order ->
                                Text(
                                    text = order.text,
                                    maxLines = 1,
                                    style = labelTextStyle
                                )
                            }

                            PagedLayout(
                                modifier = Modifier.padding(top = COMMON_PADDING),
                                activePage = activePage,
                                lastPage = lastPage,
                                pageSizeList = SearchContentPageViewModel.PageSizeList,
                                pageSizeIndex = pageSizeIndex,
                                onPreClick = { mPageViewModel.setPage(activePage - 1) },
                                onNextClick = { mPageViewModel.setPage(activePage + 1) },
                                onJumpAction = { mPageViewModel.setPage(it) }
                            ) { index, item ->
                                mPageViewModel.setPageSizeIndex(index)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onTabReselected() {
        scrollToTop()
    }

    private fun clearEditFocusAndHideSoftInput() {
        val imm = ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
        imm.hideSoftInputFromWindow(requireView().windowToken, 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollToTop = EMPTY_BLOCK
    }

    override fun onChanged(viewLifecycleOwner: LifecycleOwner) {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            mMainViewModel.word.observe(viewLifecycleOwner) {
                mPageViewModel.setWord(it)
            }
        }
    }
}