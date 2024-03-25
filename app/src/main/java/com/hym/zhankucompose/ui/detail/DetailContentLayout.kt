package com.hym.zhankucompose.ui.detail

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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import com.hym.zhankucompose.R
import com.hym.zhankucompose.compose.COMMON_PADDING
import com.hym.zhankucompose.ui.photoviewer.UrlPhotoInfo
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlin.math.roundToInt

/**
 * @author hehua2008
 * @date 2024/3/17
 */
@Composable
fun DetailContentLayout(
    detailContents: ImmutableList<DetailContent<*>>,
    onImageClick: (list: List<UrlPhotoInfo>, index: Int) -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState(),
    headerContent: @Composable ((modifier: Modifier) -> Unit)? = null
) {
    val loadingPainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_image)
    )
    val failurePainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_image_broken)
    )
    val sizeCache = remember(detailContents) {
        MutableList<IntSize?>(detailContents.size) { null }
    }

    val imageList = remember(detailContents) {
        detailContents.filterIsInstance<DetailImage>().toImmutableList()
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
                when (val detailContent = detailContents[index]) {
                    is DetailImage -> {
                        val size = sizeCache[index] ?: detailContent.data.run {
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
                            onGetSize = { sizeCache[index] = it }
                        ) { detailImage ->
                            onImageClick(photoInfos, imageList.indexOf(detailImage))
                        }
                    }

                    is DetailVideo -> {
                        DetailContentVideo(detailVideo = detailContent)
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
