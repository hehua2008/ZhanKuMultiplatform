package com.hym.zhankucompose.ui.main

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hym.zhankucompose.MyAppViewModel
import com.hym.zhankucompose.getAppViewModel
import com.hym.zhankucompose.ui.search.SearchPage
import com.hym.zhankucompose.ui.theme.ComposeTheme
import kotlinx.coroutines.flow.collectLatest

/**
 * @author hehua2008
 * @date 2024/6/29
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    ComposeTheme {
        val mainViewModel = viewModel<MainViewModel>()
        val density = LocalDensity.current
        val systemBarsTop = WindowInsets.systemBars.getTop(density)
        val paddingTop = remember(density, systemBarsTop) {
            with(density) { systemBarsTop.toDp() }
        }

        Column {
            val categoryItems = getAppViewModel<MyAppViewModel>().categoryItems
            val pagerState = rememberPagerState(mainViewModel.selectedPage) {
                1 + categoryItems.size
            }

            LaunchedEffect(mainViewModel, pagerState) {
                snapshotFlow { mainViewModel.selectedPage }
                    .collectLatest {
                        pagerState.animateScrollToPage(it)
                    }
            }

            SecondaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                val tabTextStyle = MaterialTheme.typography.titleMedium

                Tab(
                    selected = (pagerState.currentPage == 0),
                    onClick = { mainViewModel.selectedPage = 0 },
                    modifier = Modifier.padding(top = paddingTop),
                    text = {
                        Text(
                            text = "🔍",
                            fontWeight = if (pagerState.currentPage == 0) FontWeight.Bold else FontWeight.Normal,
                            style = tabTextStyle
                        )
                    }
                )
                categoryItems.forEachIndexed { index, topCate ->
                    Tab(
                        selected = (pagerState.currentPage == 1 + index),
                        onClick = { mainViewModel.selectedPage = 1 + index },
                        modifier = Modifier.padding(top = paddingTop),
                        text = {
                            Text(
                                text = topCate.name,
                                fontWeight = if (pagerState.currentPage == 1 + index) FontWeight.Bold else FontWeight.Normal,
                                style = tabTextStyle
                            )
                        }
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.zIndex(-1f), // Fix PullToRefresh overlap issue
                beyondBoundsPageCount = 1
            ) { page ->
                when (page) {
                    0 -> SearchPage()

                    else -> PreviewItemPage(topCate = categoryItems[page - 1])
                }
            }
        }
    }
}
