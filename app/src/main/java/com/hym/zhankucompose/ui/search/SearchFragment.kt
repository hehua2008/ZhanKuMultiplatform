package com.hym.zhankucompose.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.databinding.FragmentSearchArticlesBinding
import com.hym.zhankucompose.databinding.FragmentSearchWorksBinding
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.ui.main.MainViewModel
import com.hym.zhankucompose.ui.theme.ComposeTheme
import kotlinx.coroutines.flow.collectLatest

class SearchFragment : Fragment() {
    companion object {
        private const val TAG = "SearchFragment"
    }

    private val mainViewModel: MainViewModel by activityViewModels()

    @OptIn(ExperimentalFoundationApi::class)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val composeView = ComposeView(requireContext())
        composeView.setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        composeView.setContent {
            ComposeTheme {
                Column {
                    SearchLayout(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = COMMON_PADDING),
                        label = { Text(text = "输入搜索词") }
                    ) { keyword ->
                        mainViewModel.setSearchWord(keyword)
                    }

                    val titles = remember {
                        listOf(ContentType.WORK.text, ContentType.ARTICLE.text)
                    }
                    val pagerState = rememberPagerState(0) { titles.size }
                    var selectedIndex by remember { mutableIntStateOf(0) }

                    LaunchedEffect(null) {
                        snapshotFlow { selectedIndex }
                            .collectLatest {
                                pagerState.animateScrollToPage(it)
                            }
                    }

                    TabRow(selectedTabIndex = pagerState.currentPage) {
                        titles.forEachIndexed { index, title ->
                            Tab(
                                selected = (pagerState.currentPage == index),
                                onClick = { selectedIndex = index },
                                text = { Text(text = title) }
                            )
                        }
                    }

                    HorizontalPager(
                        state = pagerState,
                        beyondBoundsPageCount = titles.size
                    ) { page ->
                        when (page) {
                            0 -> AndroidViewBinding(FragmentSearchWorksBinding::inflate) {
                                // Nested scrolling interop is enabled when nested scroll is enabled
                                // for the root View
                                ViewCompat.setNestedScrollingEnabled(searchWorksContainer, true)
                                val worksFragment =
                                    searchWorksContainer.getFragment<SearchContentFragment>()
                                worksFragment?.setContentType(ContentType.WORK)
                            }

                            1 -> AndroidViewBinding(FragmentSearchArticlesBinding::inflate) {
                                // Nested scrolling interop is enabled when nested scroll is enabled
                                // for the root View
                                ViewCompat.setNestedScrollingEnabled(searchArticlesContainer, true)
                                val articlesFragment =
                                    searchArticlesContainer.getFragment<SearchContentFragment>()
                                articlesFragment?.setContentType(ContentType.ARTICLE)
                            }
                        }
                    }
                }
            }
        }
        return composeView
    }
}