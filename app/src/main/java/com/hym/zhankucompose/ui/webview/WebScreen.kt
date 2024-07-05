package com.hym.zhankucompose.ui.webview

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.navigation.LocalNavController
import com.hym.zhankucompose.ui.theme.ComposeTheme

/**
 * @author hehua2008
 * @date 2024/7/3
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebScreen(initialUrl: String, initialTitle: String = "") {
    ComposeTheme {
        val navController = LocalNavController.current
        val density = LocalDensity.current
        val systemBarsTop = WindowInsets.systemBars.getTop(density)
        val topAppBarHeight = remember(density, systemBarsTop) {
            with(density) { systemBarsTop.toDp() } + 36.dp
        }
        var barTitle by remember { mutableStateOf(initialTitle) }
        var onBackClick: () -> Boolean by remember { mutableStateOf({ false }) }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.height(topAppBarHeight),
                    title = {
                        Box(modifier = Modifier.fillMaxHeight()) {
                            Text(
                                text = barTitle,
                                modifier = Modifier.align(Alignment.CenterStart),
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1
                            )
                        }
                    },
                    navigationIcon = {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.vector_arrow_back),
                            contentDescription = null,
                            modifier = Modifier
                                .clickable {
                                    if (!onBackClick()) {
                                        navController.popBackStack()
                                    }
                                }
                                .fillMaxHeight()
                                .padding(horizontal = 12.dp)
                        )
                    }
                )
            }
        ) { innerPadding ->
            var isStatusVisible by remember { mutableStateOf(false) }
            var progress by remember { mutableIntStateOf(0) }
            val pullRefreshState = rememberPullToRefreshState()
            var refresh: () -> Unit by remember { mutableStateOf(EMPTY_BLOCK) }

            LaunchedEffect(pullRefreshState) {
                snapshotFlow { pullRefreshState.isRefreshing }
                    .collect {
                        if (it) {
                            refresh()
                        }
                    }
            }

            if (!isStatusVisible) {
                pullRefreshState.endRefresh()
            }

            // innerPadding contains inset information for you to use and apply
            Box(
                modifier = Modifier
                    // consume insets as scaffold doesn't do it by default
                    .padding(innerPadding)
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
            ) {
                WebContent(
                    initialUrl = initialUrl,
                    updateStatusVisibility = {
                        isStatusVisible = it
                    },
                    updateTitle = {
                        barTitle = it
                    },
                    updateProgress = {
                        progress = it
                    },
                    setOnBackClick = {
                        onBackClick = it
                    },
                    setOnRefreshing = {
                        refresh = it
                    },
                    modifier = Modifier.fillMaxSize()
                )

                if (isStatusVisible) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                    )
                }

                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    }
}
