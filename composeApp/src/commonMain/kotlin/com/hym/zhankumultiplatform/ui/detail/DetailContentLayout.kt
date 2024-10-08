package com.hym.zhankumultiplatform.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.hym.zhankumultiplatform.compose.COMMON_PADDING
import com.hym.zhankumultiplatform.photo.UrlPhotoInfo
import com.hym.zhankumultiplatform.player.PlayerProvider
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import org.jetbrains.compose.resources.vectorResource
import zhankumultiplatform.composeapp.generated.resources.Res
import zhankumultiplatform.composeapp.generated.resources.vector_image
import zhankumultiplatform.composeapp.generated.resources.vector_image_broken
import zhankumultiplatform.composeapp.generated.resources.vector_pause_circle
import zhankumultiplatform.composeapp.generated.resources.vector_play_circle
import zhankumultiplatform.composeapp.generated.resources.vector_replay_circle
import kotlin.math.roundToInt

/**
 * @author hehua2008
 * @date 2024/3/17
 */
@Composable
fun DetailContentLayout(
    detailContents: ImmutableList<DetailContent<*>>,
    onImageClick: (list: ImmutableList<UrlPhotoInfo>, index: Int) -> Unit,
    playerProvider: PlayerProvider,
    onVideoPlayFailed: (detailVideo: DetailVideo) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    headerContent: @Composable ((modifier: Modifier) -> Unit)? = null
) {
    val loadingPainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_image)
    )
    val failurePainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_image_broken)
    )
    val sizeCache = remember(detailContents) {
        mutableMapOf<DetailContent<*>, IntSize>()
    }

    val (imageList, nearestImageIndexes) = remember(detailContents) {
        val images = mutableListOf<DetailImage>()
        val imageIndexesInContents = mutableListOf<Int>()

        for (index in 0..detailContents.size - 1) {
            val detailContent = detailContents[index]
            if (detailContent is DetailImage) {
                images.add(detailContent)
                imageIndexesInContents.add(index)
            }
        }

        if (images.isEmpty()) {
            return@remember images.toImmutableList() to IntArray(0)
        }

        val nearestImageIndexesInImages = IntArray(detailContents.size)

        // Head: 0
        val firstImageIndexInContents = imageIndexesInContents[0]
        for (index in 0..firstImageIndexInContents) {
            nearestImageIndexesInImages[index] = 0
        }

        // Center: 1 .. imageIndexesInContents.size - 2
        for (imageIndexInImages in 1..imageIndexesInContents.size - 2) {
            val previousImageIndexInContents = imageIndexesInContents[imageIndexInImages - 1]
            val imageIndexInContents = imageIndexesInContents[imageIndexInImages]
            val center = (previousImageIndexInContents + imageIndexInContents) / 2
            for (index in previousImageIndexInContents + 1..center) {
                nearestImageIndexesInImages[index] = imageIndexInImages - 1
            }
            for (index in center + 1..imageIndexInContents) {
                nearestImageIndexesInImages[index] = imageIndexInImages
            }
        }

        // Tail: imageIndexesInContents.size - 1
        val lastImageIndexInImages = imageIndexesInContents.size - 1
        val lastImageIndexesInContents = imageIndexesInContents[lastImageIndexInImages]
        for (index in lastImageIndexesInContents + 1..nearestImageIndexesInImages.size - 1) {
            nearestImageIndexesInImages[index] = lastImageIndexInImages
        }

        images.toImmutableList() to nearestImageIndexesInImages
    }
    val photoInfos = remember(imageList) {
        imageList.map {
            UrlPhotoInfo(
                original = it.data.oriUrl,
                thumb = it.data.url,
                width = it.data.oriWidth,
                height = it.data.oriHeight
            )
        }.toImmutableList()
    }

    val playPainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_play_circle)
    )
    val pausePainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_pause_circle)
    )
    val replayPainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_replay_circle)
    )
    val playPositionMap = remember(detailContents) {
        mutableMapOf<DetailVideo, Long>()
    }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val bodyTextStyle = MaterialTheme.typography.bodyMedium.let {
        remember(it, onSurfaceColor) {
            it.copy(color = onSurfaceColor)
        }
    }

    BoxWithConstraints {
        val maxWidth = constraints.maxWidth

        LazyColumn(
            modifier = modifier,
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(COMMON_PADDING),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (headerContent != null) {
                item(key = "HeaderContent") {
                    headerContent(Modifier)
                }
            }

            items(
                count = detailContents.size,
                key = { detailContents[it].id },
                contentType = {
                    when (detailContents[it]) {
                        is DetailImage -> DetailContent.CONTENT_IMAGE
                        is DetailVideo -> DetailContent.CONTENT_VIDEO
                        is DetailText -> DetailContent.CONTENT_TEXT
                    }
                }
            ) { index ->
                // TODO: Preload image
                val detailContent = detailContents[index]
                when (detailContent) {
                    is DetailImage -> {
                        val size = sizeCache[detailContent] ?: detailContent.data.run {
                            if (maxWidth != Constraints.Infinity && width > 0 && height > 0) {
                                IntSize(
                                    maxWidth, (maxWidth * height / width.toFloat()).roundToInt()
                                )
                            } else null
                        }

                        DetailContentImage(
                            detailImage = detailContent,
                            loadingPainter = loadingPainter,
                            failurePainter = failurePainter,
                            size = size,
                            onGetSize = { sizeCache[detailContent] = it }
                        ) { detailImage ->
                            onImageClick(photoInfos, imageList.indexOf(detailImage))
                        }
                    }

                    is DetailVideo -> {
                        val size = sizeCache[detailContent] ?: detailContent.data.run {
                            if (maxWidth != Constraints.Infinity && width > 0 && height > 0) {
                                IntSize(
                                    maxWidth, (maxWidth * height / width.toFloat()).roundToInt()
                                )
                            } else null
                        }

                        DetailContentVideo(
                            detailVideo = detailContent,
                            playPainter = playPainter,
                            pausePainter = pausePainter,
                            replayPainter = replayPainter,
                            playerProvider = playerProvider,
                            onVideoPlayFailed = onVideoPlayFailed,
                            size = size,
                            onGetSize = { sizeCache[detailContent] = it },
                            playPosition = playPositionMap[detailContent]
                        ) { detailVideo, position ->
                            playPositionMap[detailVideo] = position
                        }
                    }

                    is DetailText -> {
                        DetailContentText(
                            detailText = detailContent,
                            modifier = Modifier.padding(horizontal = COMMON_PADDING),
                            textStyle = bodyTextStyle
                        )
                    }
                }
            }

            item(key = "BottomContent") {
                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                )
            }
        }
    }
}
