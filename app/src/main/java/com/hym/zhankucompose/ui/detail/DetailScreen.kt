package com.hym.zhankucompose.ui.detail

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.rememberMutableState
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.CreatorObj
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.navigation.LocalNavController
import com.hym.zhankucompose.photo.UrlPhotoInfo
import com.hym.zhankucompose.ui.ThemeColorRetriever
import com.hym.zhankucompose.ui.theme.ComposeTheme
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

/**
 * @author hehua2008
 * @date 2024/6/29
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    contentType: ContentType,
    contentId: String,
    onNavigateToTagList: (author: CreatorObj?, topCate: TopCate?, subCate: SubCate?) -> Unit,
    onNavigateToImagePager: (photoInfos: ImmutableList<UrlPhotoInfo>, currentPosition: Int) -> Unit,
    onNavigateToWebView: (url: String, title: String) -> Unit
) {
    ComposeTheme {
        val navController = LocalNavController.current
        val detailViewModel = viewModel<DetailViewModel>(key = contentId)
        var title by remember { mutableStateOf("") }
        val pullRefreshState = rememberPullToRefreshState()

        LaunchedEffect(detailViewModel) {
            detailViewModel.setDetailTypeAndId(contentType, contentId)
        }

        LaunchedEffect(detailViewModel, pullRefreshState) {
            snapshotFlow { pullRefreshState.isRefreshing }
                .collect {
                    if (it) {
                        detailViewModel.setDetailTypeAndId(contentType, contentId)
                    }
                }
        }

        when (detailViewModel.loadState) {
            is LoadState.NotLoading -> pullRefreshState.endRefresh()
            is LoadState.Error -> pullRefreshState.endRefresh()
            //is LoadState.Loading -> pullRefreshState.startRefresh()
            else -> {}
        }

        var detailContents: ImmutableList<DetailContent<*>>? = null
        var headerContent: @Composable ((modifier: Modifier) -> Unit)? = null

        when (contentType) {
            ContentType.WORK -> {
                detailViewModel.workDetails?.let { work ->
                    title = work.product.title

                    detailContents = remember(work) {
                        (work.product.productVideos.map { video ->
                            DetailVideo(video)
                        } + work.product.productImages.map { image ->
                            DetailImage(image)
                        }).toImmutableList()
                    }

                    headerContent = { modifier ->
                        val categories = remember(work) {
                            listOf(work.product.fieldCateObj, work.product.subCateObj)
                                .toImmutableList()
                        }

                        DetailHeaderLayout(
                            titleStr = work.product.title,
                            categories = categories,
                            creatorObj = work.product.creatorObj,
                            linkUrl = work.product.pageUrl,
                            timeStr = work.product.updateTimeStr,
                            viewCountStr = work.product.viewCountStr,
                            commentCountStr = work.product.commentCountStr,
                            favoriteCountStr = "${work.product.favoriteCount}",
                            shareWordsStr = work.sharewords,
                            onNavigateToTagList = onNavigateToTagList,
                            onNavigateToWebView = onNavigateToWebView,
                            modifier = modifier
                        ) {
                            /* TODO
                            DownloadWorker.enqueue(
                                this, work.product.productImages.map { it.oriUrl }
                            )
                            */
                        }
                    }
                }
            }

            ContentType.ARTICLE -> {
                detailViewModel.articleDetails?.let { article ->
                    title = article.articledata.title

                    detailContents = remember(article) {
                        DetailContent.articleDetailsToDetailContent(article)
                            .toImmutableList()
                    }

                    headerContent = { modifier ->
                        val categories = remember(article) {
                            article.articledata.articleCates.toImmutableList()
                        }

                        DetailHeaderLayout(
                            titleStr = article.articledata.title,
                            categories = categories,
                            creatorObj = article.articledata.creatorObj,
                            linkUrl = article.articledata.pageUrl,
                            timeStr = article.articledata.updateTimeStr,
                            viewCountStr = article.articledata.viewCountStr,
                            commentCountStr = article.articledata.commentCountStr,
                            favoriteCountStr = "${article.articledata.favoriteCount}",
                            shareWordsStr = article.sharewords,
                            onNavigateToTagList = onNavigateToTagList,
                            onNavigateToWebView = onNavigateToWebView,
                            modifier = modifier
                        )
                    }
                }
            }
        }

        val lazyListState = rememberLazyListState()
        val density = LocalDensity.current
        val systemBarsTop = WindowInsets.systemBars.getTop(density)
        val topAppBarHeight = remember(density, systemBarsTop) {
            with(density) { systemBarsTop.toDp() } + 36.dp
        }
        val topAppBarColors = remember(detailViewModel.themeColor) {
            detailViewModel.themeColor?.let {
                val containerColor = Color(it.color)
                val titleContentColor = Color(it.titleTextColor)
                TopAppBarColors(
                    containerColor = containerColor,
                    scrolledContainerColor = containerColor,
                    navigationIconContentColor = titleContentColor,
                    titleContentColor = titleContentColor,
                    actionIconContentColor = titleContentColor
                )
            }
        }
        val composeScope = rememberCoroutineScope()
        var fabPosition by rememberMutableState { FabPosition.Center }

        Scaffold(
            topBar = {
                TopAppBar(
                    modifier = Modifier.height(topAppBarHeight),
                    title = {
                        Box(modifier = Modifier.fillMaxHeight()) {
                            Text(
                                text = title,
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
                                    navController.popBackStack()
                                }
                                .fillMaxHeight()
                                .padding(horizontal = 12.dp)
                        )
                    },
                    colors = topAppBarColors ?: TopAppBarDefaults.topAppBarColors()
                )
            },
            floatingActionButton = {
                var centerPositionX by rememberMutableState { 0f }
                var parentWidth by rememberMutableState<Int?> { null }
                var offsetX by remember { mutableFloatStateOf(0f) }

                FloatingActionButton(
                    onClick = {
                        composeScope.launch {
                            lazyListState.animateScrollToItem(0)
                        }
                    },
                    modifier = Modifier
                        .onGloballyPositioned {
                            centerPositionX = it.positionInParent().x + (it.size.width / 2f)
                            parentWidth = it.parentLayoutCoordinates?.size?.width
                        }
                        .offset {
                            IntOffset(offsetX.roundToInt(), 0)
                        }
                        .draggable(
                            state = rememberDraggableState { delta ->
                                offsetX += delta
                            },
                            orientation = Orientation.Horizontal,
                            onDragStopped = {
                                parentWidth?.let {
                                    val newCenterPositionX = centerPositionX + offsetX
                                    fabPosition = when {
                                        newCenterPositionX < it / 3f -> FabPosition.Start
                                        newCenterPositionX > it * 2 / 3f -> FabPosition.End
                                        else -> FabPosition.Center
                                    }
                                }
                                offsetX = 0f
                            }
                        ),
                    shape = CircleShape
                ) {
                    Icon(ImageVector.vectorResource(R.drawable.vector_rocket), "")
                }
            },
            floatingActionButtonPosition = fabPosition
        ) { innerPadding ->
            // innerPadding contains inset information for you to use and apply
            Box(
                modifier = Modifier
                    // consume insets as scaffold doesn't do it by default
                    .padding(innerPadding)
                    .nestedScroll(pullRefreshState.nestedScrollConnection)
            ) {
                DetailContentLayout(
                    detailContents = detailContents ?: persistentListOf(),
                    lazyListState = lazyListState,
                    onImageClick = { list, index ->
                        onNavigateToImagePager(list, index)
                    },
                    playerProvider = detailViewModel.playerProvider,
                    onVideoPlayFailed = { detailVideo ->
                        onNavigateToWebView(detailVideo.data.url, detailVideo.data.name)
                    }
                ) {
                    headerContent?.invoke(
                        it.padding(
                            top = COMMON_PADDING,
                            start = COMMON_PADDING,
                            end = COMMON_PADDING
                        )
                    )
                }

                PullToRefreshContainer(
                    state = pullRefreshState,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }

        LaunchedEffect(detailViewModel, detailContents) {
            if (detailViewModel.themeColor != null) return@LaunchedEffect
            val firstImage = detailContents?.filterIsInstance<DetailImage>()?.firstOrNull()
                ?: return@LaunchedEffect
            ThemeColorRetriever.getMainThemeColor(firstImage.data.urlSmall)?.let {
                detailViewModel.themeColor = it
            }
        }

        val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle
        val currentIndexState = remember(savedStateHandle) {
            savedStateHandle?.getStateFlow<Int?>("PHOTO_INDEX", null)
        }?.collectAsState(initial = null)

        LaunchedEffect(currentIndexState) {
            snapshotFlow { currentIndexState?.value }
                .collectLatest {
                    val position = it ?: return@collectLatest
                    lazyListState.scrollToItem(if (headerContent == null) position else position + 1)
                }
        }
    }
}
