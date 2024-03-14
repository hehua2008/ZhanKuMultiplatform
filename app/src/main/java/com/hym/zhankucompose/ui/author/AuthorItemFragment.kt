package com.hym.zhankucompose.ui.author

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.compose.SimpleLinkText
import com.hym.zhankucompose.compose.SimpleRadioGroup
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.ui.PagedLayout
import com.hym.zhankucompose.ui.TabReselectedCallback
import com.hym.zhankucompose.ui.main.PreviewLayout
import com.hym.zhankucompose.ui.theme.ComposeTheme
import com.hym.zhankucompose.ui.webview.WebViewActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.toImmutableList

@AndroidEntryPoint
class AuthorItemFragment : Fragment(), Observer<LifecycleOwner>, TabReselectedCallback {
    companion object {
        private const val TAG = "AuthorItemFragment"
        const val AUTHOR = "AUTHOR"

        @JvmStatic
        fun newInstance(author: CreatorObj): AuthorItemFragment {
            val fragment = AuthorItemFragment()
            fragment.arguments = Bundle().apply {
                putParcelable(AUTHOR, author)
            }
            return fragment
        }
    }

    private val mPageViewModel: AuthorPageViewModel by viewModels()
    private lateinit var mAuthor: CreatorObj

    private var scrollToTop: () -> Unit = EMPTY_BLOCK

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(AUTHOR, mAuthor)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewLifecycleOwnerLiveData.observe(this, this)

        val activeBundle = savedInstanceState ?: arguments
        mAuthor = activeBundle!!.getParcelable(AUTHOR)!!
        mPageViewModel.authorUid = mAuthor.id
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
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

                    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
                    val labelTextStyle = MaterialTheme.typography.labelLarge.let {
                        remember(it, onSurfaceColor) { it.copy(color = onSurfaceColor) }
                    }

                    val sortOrders = remember {
                        listOf(
                            SortOrder.LATEST_PUBLISH,
                            SortOrder.MOST_RECOMMEND,
                            SortOrder.MOST_FAVORITE,
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
                            SimpleLinkText(
                                link = "https://www.zcool.com.cn/u/${mPageViewModel.authorUid}",
                                modifier = Modifier.padding(top = COMMON_PADDING)
                            ) {
                                val intent = Intent(localContext, WebViewActivity::class.java)
                                    .putExtra(WebViewActivity.WEB_URL, it)
                                    .putExtra(WebViewActivity.WEB_TITLE, mAuthor.username)
                                localContext.startActivity(intent)
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
                                pageSizeList = AuthorPageViewModel.PageSizeList,
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
    }
}