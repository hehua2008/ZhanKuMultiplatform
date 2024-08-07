package com.hym.zhankumultiplatform.ui.main

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
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hym.zhankumultiplatform.MyAppViewModel
import com.hym.zhankumultiplatform.getAppViewModel
import com.hym.zhankumultiplatform.ui.search.SearchPage
import kotlinx.coroutines.launch

/**
 * @author hehua2008
 * @date 2024/6/29
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val mainViewModel = viewModel<MainViewModel> { MainViewModel() }
    val density = LocalDensity.current
    val systemBarsTop = WindowInsets.systemBars.getTop(density)
    val paddingTop = remember(density, systemBarsTop) {
        with(density) { systemBarsTop.toDp() }
    }

    Column(modifier = modifier) {
        val categoryItems = getAppViewModel<MyAppViewModel>().categoryItems
        val pagerState = rememberPagerState(if (categoryItems.isEmpty()) 0 else 1) {
            1 + categoryItems.size
        }
        val scope = rememberCoroutineScope()

        SecondaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
            val tabTextStyle = MaterialTheme.typography.titleMedium

            Tab(
                selected = (pagerState.currentPage == 0),
                onClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(0)
                    }
                },
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
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1 + index)
                        }
                    },
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
