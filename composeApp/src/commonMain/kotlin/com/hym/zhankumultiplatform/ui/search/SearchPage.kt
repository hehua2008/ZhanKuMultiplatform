package com.hym.zhankumultiplatform.ui.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hym.zhankumultiplatform.compose.COMMON_PADDING
import com.hym.zhankumultiplatform.model.ContentType
import com.hym.zhankumultiplatform.ui.main.MainViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * @author hehua2008
 * @date 2024/4/23
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchPage(
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel = viewModel { MainViewModel() }
) {
    Column(modifier = modifier) {
        SearchLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = COMMON_PADDING),
            label = { Text(text = "输入搜索词") }
        ) { keyword ->
            mainViewModel.setSearchWord(keyword)
        }

        val titles = remember { listOf(ContentType.WORK.text, ContentType.ARTICLE.text) }
        val pagerState = rememberPagerState(0) { titles.size }
        var selectedIndex by remember { mutableIntStateOf(0) }

        LaunchedEffect(pagerState) {
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
            modifier = Modifier.zIndex(-1f),
            beyondBoundsPageCount = titles.size
        ) { page ->
            when (page) {
                0 -> SearchContentPage(
                    contentType = ContentType.WORK,
                    mainViewModel = mainViewModel
                )

                1 -> SearchContentPage(
                    contentType = ContentType.ARTICLE,
                    mainViewModel = mainViewModel
                )
            }
        }
    }
}
