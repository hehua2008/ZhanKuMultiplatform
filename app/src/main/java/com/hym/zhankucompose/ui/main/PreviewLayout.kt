package com.hym.zhankucompose.ui.main

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.bumptech.glide.Glide
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.EMPTY_BLOCK
import com.hym.zhankucompose.compose.FlingVelocityListener
import com.hym.zhankucompose.compose.NestedScrollConnectionDelegate
import com.hym.zhankucompose.compose.listenableFlingBehavior
import com.hym.zhankucompose.compose.plus
import com.hym.zhankucompose.compose.rememberMutableFloatState
import com.hym.zhankucompose.compose.rememberMutableState
import com.hym.zhankucompose.model.Content
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * @author hehua2008
 * @date 2024/3/9
 */
private const val TAG = "PreviewLayout"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewLayout(
    lazyPagingItems: LazyPagingItems<Content>,
    modifier: Modifier = Modifier,
    setOnScrollToTopAction: ((onScrollToTopAction: () -> Unit) -> Unit)? = null,
    headerContent: @Composable ((headerModifier: Modifier) -> Unit)? = null
) {
    val pullRefreshState = rememberPullToRefreshState()
    val lazyGridState = rememberLazyGridState() // It won't recreated
    var showFab by remember { mutableStateOf(false) }

    LaunchedEffect(pullRefreshState) {
        snapshotFlow { pullRefreshState.isRefreshing }
            .collect {
                if (it) {
                    lazyPagingItems.refresh()
                }
            }
    }

    LaunchedEffect(lazyPagingItems) {
        snapshotFlow { lazyPagingItems.loadState.refresh }
            .collectLatest {
                when (it) {
                    is LoadState.Loading -> {
                        pullRefreshState.startRefresh()
                    }

                    is LoadState.NotLoading -> {
                        pullRefreshState.endRefresh()
                        showFab = false
                    }

                    is LoadState.Error -> {
                        pullRefreshState.endRefresh()
                    }
                }
            }
    }

    val composeScope = rememberCoroutineScope()

    if (setOnScrollToTopAction != null) {
        DisposableEffect(setOnScrollToTopAction) {
            setOnScrollToTopAction {
                composeScope.launch {
                    lazyGridState.scrollToItem(0)
                    showFab = false
                }
            }

            onDispose {
                setOnScrollToTopAction(EMPTY_BLOCK)
            }
        }
    }

    val density = LocalDensity.current
    val startFastScrollVelocityThreshold by rememberMutableFloatState(density) {
        with(density) { 4000.dp.toPx() }
    }
    val stopFastScrollVelocityThreshold by rememberMutableFloatState(density) {
        with(density) { 1000.dp.toPx() }
    }
    val context = LocalContext.current
    val requestManager by rememberMutableState(context) { Glide.with(context) }
    val flingVelocityListener = remember {
        // startFastScrollVelocityThreshold, stopFastScrollVelocityThreshold and requestManager will
        // always refer to the latest value.
        object : FlingVelocityListener {
            var fastScrollStarted = false

            override fun onStartFling(initialVelocity: Float) {
                if (abs(initialVelocity) >= startFastScrollVelocityThreshold) {
                    fastScrollStarted = true
                    Log.d(TAG, "startFastScroll at $initialVelocity px/sec")
                    requestManager.pauseRequestsRecursive()
                }
                if (!lazyGridState.canScrollForward ||
                    (initialVelocity < 0f && lazyGridState.canScrollBackward)
                ) {
                    showFab = true
                }
            }

            override fun onFlingVelocityDecayed(remainingVelocity: Float) {
                if (fastScrollStarted && abs(remainingVelocity) < stopFastScrollVelocityThreshold) {
                    fastScrollStarted = false
                    Log.d(TAG, "stopFastScroll at $remainingVelocity px/sec")
                    requestManager.resumeRequestsRecursive()
                }
            }

            override fun onEndFling() {
                if (fastScrollStarted) {
                    fastScrollStarted = false
                    Log.d(TAG, "stopFastScroll")
                    requestManager.resumeRequestsRecursive()
                }
            }
        }
    }

    // Add the nested scroll connection to your top level @Composable element using the
    // nestedScroll modifier.
    val nestedScrollInterop = rememberNestedScrollInteropConnection()
    val combinedNestedScrollConnection = remember(nestedScrollInterop, pullRefreshState) {
        object : NestedScrollConnectionDelegate(
            nestedScrollInterop + pullRefreshState.nestedScrollConnection
        ) {
            override fun afterPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
                selfConsumed: Offset
            ) {
                if (source != NestedScrollSource.Fling && consumed.y < 0f) {
                    showFab = false
                }
            }
        }
    }

    Box(modifier = modifier.nestedScroll(combinedNestedScrollConnection)) {
        PreviewItemGrid(
            lazyPagingItems = lazyPagingItems,
            modifier = Modifier.fillMaxSize(),
            columnSize = 2,
            lazyGridState = lazyGridState,
            flingBehavior = listenableFlingBehavior(flingVelocityListener),
            headerContent = headerContent
        )

        PullToRefreshContainer(
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter)
        )

        val fabOnClick: () -> Unit = remember {
            {
                composeScope.launch {
                    lazyGridState.scrollToItem(0)
                    showFab = false
                }
            }
        }

        AnimatedVisibility(
            visible = showFab,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp),
            enter = remember { scaleIn() + fadeIn() },
            exit = remember { scaleOut() + fadeOut() }
        ) {
            FloatingActionButton(
                onClick = fabOnClick,
                shape = CircleShape
            ) {
                Icon(ImageVector.vectorResource(R.drawable.vector_rocket), "")
            }
        }
    }
}
