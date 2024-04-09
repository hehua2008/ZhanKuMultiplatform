package com.hym.zhankucompose.ui.main

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.compose.ui.zIndex
import androidx.core.view.ViewCompat
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.MyAppViewModel
import com.hym.zhankucompose.databinding.FragmentSearchBinding
import com.hym.zhankucompose.getAppViewModel
import com.hym.zhankucompose.ui.theme.ComposeTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MainActivity : BaseActivity() {
    companion object {
        private const val TAG = "MainActivity"
    }

    @OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        // Add callback before fragmentManager
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                moveTaskToBack(false)
            }
        })

        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        setContent {
            ComposeTheme {
                val density = LocalDensity.current
                val systemBarsTop = WindowInsets.systemBars.getTop(density)
                val paddingTop = remember(density, systemBarsTop) {
                    with(density) { systemBarsTop.toDp() }
                }

                Column {
                    val categoryItems by getAppViewModel<MyAppViewModel>().categoryItems.observeAsState(
                        persistentListOf()
                    )

                    val pagerState = rememberPagerState(0) { 1 + categoryItems.size }
                    var selectedIndex by remember { mutableIntStateOf(0) }

                    LaunchedEffect(null) {
                        snapshotFlow { categoryItems }
                            .collectLatest {
                                selectedIndex = if (categoryItems.isEmpty()) 0 else 1
                            }
                    }

                    LaunchedEffect(pagerState) {
                        snapshotFlow { selectedIndex }
                            .collectLatest {
                                pagerState.animateScrollToPage(it)
                            }
                    }

                    SecondaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
                        val tabTextStyle = MaterialTheme.typography.titleMedium

                        Tab(
                            selected = (pagerState.currentPage == 0),
                            onClick = { selectedIndex = 0 },
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
                                onClick = { selectedIndex = 1 + index },
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
                        beyondBoundsPageCount = 1 + categoryItems.size
                    ) { page ->
                        when (page) {
                            0 -> AndroidViewBinding(
                                factory = FragmentSearchBinding::inflate,
                                onReset = {}
                            ) {
                                // Nested scrolling interop is enabled when nested scroll is enabled
                                // for the root View
                                ViewCompat.setNestedScrollingEnabled(searchContainer, true)
                            }

                            else -> PreviewItemPage(topCate = categoryItems[page - 1])
                        }
                    }
                }
            }
        }
    }
}
