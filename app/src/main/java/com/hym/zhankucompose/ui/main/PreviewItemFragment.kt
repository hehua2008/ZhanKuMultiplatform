package com.hym.zhankucompose.ui.main

import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.paging.compose.collectAsLazyPagingItems
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.compose.LabelFlowLayout
import com.hym.zhankucompose.compose.SMALL_PADDING_VALUES
import com.hym.zhankucompose.compose.SimpleLinkText
import com.hym.zhankucompose.compose.SimpleRadioGroup
import com.hym.zhankucompose.model.Cate
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.ui.PagedLayout
import com.hym.zhankucompose.ui.TabReselectedCallback
import com.hym.zhankucompose.ui.theme.ComposeTheme
import com.hym.zhankucompose.ui.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toImmutableList

@AndroidEntryPoint
class PreviewItemFragment : Fragment(), Observer<LifecycleOwner>, TabReselectedCallback {
    companion object {
        private const val TAG = "PreviewItemFragment"
        const val TOP_CATE = "TOP_CATE"
        const val SUB_CATE = "SUB_CATE"

        @JvmStatic
        fun newInstance(topCate: TopCate? = null, subCate: SubCate? = null): PreviewItemFragment {
            val fragment = PreviewItemFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(TOP_CATE, topCate)
                putParcelable(SUB_CATE, subCate)
            }
            return fragment
        }
    }

    var topCate: TopCate? = null
        private set
    private var mSubCate: SubCate? = null
    private val mPageViewModel: PreviewPageViewModel by viewModels()

    private var scrollToTop: () -> Unit = EMPTY_BLOCK

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(TOP_CATE, topCate)
        outState.putParcelable(SUB_CATE, mSubCate)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwnerLiveData.observe(this, this)

        val activeBundle = savedInstanceState ?: arguments
        topCate = activeBundle!!.getParcelable(TOP_CATE)
        mSubCate = activeBundle.getParcelable(SUB_CATE)
        mPageViewModel.topCate = topCate
        mPageViewModel.setSubCate(mSubCate)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        /*
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorAccent, typedValue, true)
        binding.swipeRefresh.setColorSchemeColors(typedValue.data)
        */
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ComposeTheme {
                    val localContext = LocalContext.current
                    val lazyPagingItems = mPageViewModel.pagingFlow.collectAsLazyPagingItems()
                    val lifecycleOwner = LocalLifecycleOwner.current

                    DisposableEffect(lazyPagingItems, lifecycleOwner) {
                        val observer = Observer<Unit> { lazyPagingItems.refresh() }
                        mPageViewModel.mediatorLiveData.observe(lifecycleOwner, observer)

                        onDispose {
                            mPageViewModel.mediatorLiveData.removeObserver(observer)
                        }
                    }

                    val topCate = mPageViewModel.topCate!!
                    val subCate by mPageViewModel.subCate.observeAsState()
                    val categoryLink = remember(subCate) {
                        updateCategoryLink(topCate, subCate)
                    }
                    val categories = remember {
                        mutableListOf<Cate>(topCate).apply {
                            addAll(topCate.subCateList)
                        }.toImmutableList()
                    }
                    val categoriesNames = remember(categories) {
                        categories.map { it.name }.toImmutableList()
                    }
                    val defaultCategoryIndex = remember(categories, subCate) {
                        categories.indexOf(subCate as Cate?).let { if (it < 0) 0 else it }
                    }

                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                    val labelTextStyle = MaterialTheme.typography.labelLarge.let {
                        remember(it, onSurfaceColor) { it.copy(color = onSurfaceColor) }
                    }

                    val recommendLevels = remember {
                        listOf(
                            RecommendLevel.EDITOR_CHOICE,
                            RecommendLevel.ALL_RECOMMEND,
                            RecommendLevel.HOME_RECOMMEND,
                            RecommendLevel.LATEST_PUBLISH
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
                            SimpleLinkText(
                                link = categoryLink,
                                modifier = Modifier.padding(top = COMMON_PADDING)
                            ) {
                                val intent = Intent(localContext, WebViewActivity::class.java)
                                    .putExtra(WebViewActivity.WEB_URL, it)
                                    .putExtra(WebViewActivity.WEB_TITLE, (subCate ?: topCate).name)
                                localContext.startActivity(intent)
                            }

                            LabelFlowLayout(
                                labels = categoriesNames,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = COMMON_PADDING),
                                defaultLabelIndex = defaultCategoryIndex,
                                itemPadding = SMALL_PADDING_VALUES
                            ) {
                                mPageViewModel.setSubCate(if (it == 0) null else topCate.subCateList[it - 1])
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

                            PagedLayout(
                                modifier = Modifier.padding(top = COMMON_PADDING),
                                activePage = activePage,
                                lastPage = lastPage,
                                pageSizeList = PreviewPageViewModel.PageSizeList,
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

    private fun updateCategoryLink(topCate: TopCate, subCate: SubCate?): String {
        val sb = StringBuilder("https://www.zcool.com.cn/discover?")
        sb.append("cate=").append(topCate.id)
        subCate?.id?.let { subCateId ->
            sb.append("&subCate=").append(subCateId)
        }
        return sb.toString()
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
            mPageViewModel.subCate.observe(viewLifecycleOwner) {
                mSubCate = it
            }
        }
    }
}