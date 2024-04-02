package com.hym.zhankucompose.ui.detail

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.viewModels
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.WindowCompat
import androidx.paging.LoadState
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.compose.rememberMutableState
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.photo.UrlPhotoInfo
import com.hym.zhankucompose.ui.ThemeColorRetriever
import com.hym.zhankucompose.ui.photoviewer.PhotoViewerActivity
import com.hym.zhankucompose.ui.theme.ComposeTheme
import com.hym.zhankucompose.ui.webview.WebViewActivity
import com.hym.zhankucompose.util.MMCQ
import com.hym.zhankucompose.work.DownloadWorker
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class DetailActivity : BaseActivity() {
    companion object {
        const val KEY_TITLE = "TITLE"
        const val KEY_CONTENT_TYPE = "CONTENT_TYPE"
        const val KEY_CONTENT_ID = "CONTENT_ID"
        const val KEY_COLOR = "COLOR"
    }

    private lateinit var mTitle: String
    private lateinit var mContentId: String
    private var mContentType = ContentType.WORK.value
    private var mThemeColor: MMCQ.ThemeColor? = null

    private val detailViewModel: DetailViewModel by viewModels()

    private lateinit var photoViewerActivityLauncher: ActivityResultLauncher<Pair<List<UrlPhotoInfo>, Int>>

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()

        super.onCreate(savedInstanceState)

        if (intent.action == Intent.ACTION_VIEW) {
            val data = intent.data!!
            mTitle = ""
            mContentId = data.lastPathSegment!!
            mContentType =
                if (data.pathSegments[0] == "work") ContentType.WORK.value else ContentType.ARTICLE.value
        } else {
            mTitle = intent.getStringExtra(KEY_TITLE)!!
            mContentId = intent.getStringExtra(KEY_CONTENT_ID)!!
            mContentType = intent.getIntExtra(KEY_CONTENT_TYPE, mContentType)
        }

        (intent.getParcelableExtra(KEY_COLOR) as? MMCQ.ThemeColor)?.let { updateThemeColor(it) }

        initPhotoViewerActivityLauncher()

        setContent {
            ComposeTheme {
                var title by remember { mutableStateOf(mTitle) }
                val pullRefreshState = rememberPullToRefreshState()

                LaunchedEffect(pullRefreshState) {
                    snapshotFlow { pullRefreshState.isRefreshing }
                        .collect {
                            if (it) {
                                loadData()
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

                when (mContentType) {
                    ContentType.WORK.value -> {
                        detailViewModel.workDetails.observeAsState().value?.let { work ->
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
                                    modifier = modifier
                                ) {
                                    DownloadWorker.enqueue(
                                        this, work.product.productImages.map { it.oriUrl }
                                    )
                                }
                            }
                        }
                    }

                    ContentType.ARTICLE.value -> {
                        detailViewModel.articleDetails.observeAsState().value?.let { article ->
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
                var themeColor: MMCQ.ThemeColor? by rememberMutableState { mThemeColor }
                val topAppBarColors = remember(themeColor) {
                    themeColor?.let {
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
                                        .clickable { finish() }
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
                                launchPhotoViewerActivity(list, index)
                            },
                            playerProvider = detailViewModel.playerProvider,
                            onVideoPlayFailed = { detailVideo ->
                                this@DetailActivity.startActivity(
                                    Intent(this@DetailActivity, WebViewActivity::class.java)
                                        .putExtra(WebViewActivity.WEB_URL, detailVideo.data.url)
                                        .putExtra(WebViewActivity.WEB_TITLE, detailVideo.data.name)
                                )
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

                LaunchedEffect(detailContents) {
                    if (themeColor != null) return@LaunchedEffect
                    val firstImage = detailContents?.filterIsInstance<DetailImage>()?.firstOrNull()
                        ?: return@LaunchedEffect
                    ThemeColorRetriever.getMainThemeColor(firstImage.data.urlSmall)?.let {
                        themeColor = it
                        updateThemeColor(it)
                    }
                }

                val localView = LocalView.current

                LaunchedEffect(
                    detailViewModel.positionAndScreenLocation, detailContents, localView
                ) {
                    val result = detailViewModel.positionAndScreenLocation ?: return@LaunchedEffect
                    val detailContentList = detailContents ?: return@LaunchedEffect
                    val detailImage =
                        detailContentList.filterIsInstance<DetailImage>().getOrNull(result.first)
                            ?: return@LaunchedEffect
                    val tmpArr = IntArray(2)
                    val screenLocation = result.second ?: localView.rootView.run {
                        getLocationOnScreen(tmpArr)
                        Rect(tmpArr[0], tmpArr[1], tmpArr[0] + width, tmpArr[1] + height)
                    }
                    val image = detailImage.data
                    val imageHeight =
                        if (image.width == 0 || image.height == 0) 0
                        else (image.height * localView.width / image.width.toFloat()).toInt()
                    val imageViewScreenTop =
                        screenLocation.top + (screenLocation.height() - imageHeight) / 2
                    localView.getLocationOnScreen(tmpArr)
                    val localViewScreenTop = tmpArr[1]
                    val offset = imageViewScreenTop - localViewScreenTop
                    val position = 1 + detailContentList.indexOf(detailImage)
                    lazyListState.scrollToItem(
                        position, -offset + with(density) { topAppBarHeight.roundToPx() }
                    )
                }
            }
        }

        loadData()
    }

    private fun updateThemeColor(themeColor: MMCQ.ThemeColor) {
        mThemeColor = themeColor
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars =
            themeColor.isDarkText
    }

    private fun loadData() {
        detailViewModel.setDetailTypeAndId(mContentType, mContentId)
    }

    private fun initPhotoViewerActivityLauncher() {
        val contract =
            object : ActivityResultContract<Pair<List<UrlPhotoInfo>, Int>, Pair<Int, Rect?>?>() {
                override fun createIntent(
                    context: Context,
                    input: Pair<List<UrlPhotoInfo>, Int>
                ): Intent {
                    return Intent(context, PhotoViewerActivity::class.java)
                        .putParcelableArrayListExtra(
                            PhotoViewerActivity.PHOTO_INFOS,
                            ArrayList(input.first)
                        )
                        .putExtra(PhotoViewerActivity.CURRENT_POSITION, input.second)
                }

                override fun parseResult(resultCode: Int, intent: Intent?): Pair<Int, Rect?>? {
                    intent ?: return null
                    val position = intent.getIntExtra(PhotoViewerActivity.CURRENT_POSITION, 0)
                    val screenLocation =
                        intent.getParcelableExtra<Rect>(PhotoViewerActivity.SCREEN_LOCATION)
                    return position to screenLocation
                }
            }

        photoViewerActivityLauncher = registerForActivityResult(contract) { result ->
            detailViewModel.positionAndScreenLocation = result
        }
    }

    fun launchPhotoViewerActivity(photoInfos: List<UrlPhotoInfo>, position: Int) {
        photoViewerActivityLauncher.launch(
            photoInfos to position,
            ActivityOptionsCompat.makeCustomAnimation(this, 0, android.R.anim.fade_out)
        )
    }
}