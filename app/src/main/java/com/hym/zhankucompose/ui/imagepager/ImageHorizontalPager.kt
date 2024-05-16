package com.hym.zhankucompose.ui.imagepager

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hym.compose.subsamplingimage.DefaultImageBitmapRegionDecoderFactory
import com.hym.compose.subsamplingimage.SubsamplingImage
import com.hym.compose.subsamplingimage.SubsamplingState
import com.hym.compose.subsamplingimage.recycle
import com.hym.compose.zoom.rememberZoomState
import com.hym.zhankucompose.R
import com.hym.zhankucompose.photo.UrlPhotoInfo
import com.hym.zhankucompose.ui.CommonViewModel
import kotlinx.collections.immutable.ImmutableList
import okio.FileSystem
import okio.Path

/**
 * @author hehua2008
 * @date 2024/4/2
 */
private const val TAG = "ImageHorizontalPager"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ImageHorizontalPager(
    commonViewModel: CommonViewModel = viewModel(),
    photoInfoList: ImmutableList<UrlPhotoInfo>,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    beyondBoundsPageCount: Int = 1,
    pagerState: PagerState = rememberPagerState(initialPage = initialIndex) { photoInfoList.size }
) {
    val loadingPainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_image)
    )
    val failurePainter = rememberVectorPainter(
        ImageVector.vectorResource(R.drawable.vector_image_broken)
    )

    HorizontalPager(
        state = pagerState,
        modifier = modifier,
        beyondBoundsPageCount = beyondBoundsPageCount
    ) { page ->
        var showLoading by remember { mutableStateOf(true) }
        var showFailure by remember { mutableStateOf(false) }

        /* It seems that there is a bug when use AnimatedVisibility in HorizontalPager
        AnimatedVisibility(
            visible = showLoading || showFailure,
            enter = remember { fadeIn() },
            exit = remember { fadeOut() }
        ) {
        */
        if (showLoading || showFailure) {
            Image(
                painter = if (showFailure) failurePainter else loadingPainter,
                contentDescription = "Placeholder",
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = MaterialTheme.colorScheme.surfaceContainer),
                contentScale = ContentScale.Inside,
                alpha = 0.5f
            )
        }

        val zoomState = rememberZoomState(
            //contentAspectRatio = photoInfo.width / photoInfo.height.toFloat(),
            minZoomScale = 0.8f,
            maxZoomScale = 8f,
            doubleClickZoomScale = 4f
        )
        val photoInfo = remember(photoInfoList, page) { photoInfoList[page] }
        var photoPath by remember { mutableStateOf<Path?>(null) }

        SubsamplingImage(
            zoomState = zoomState,
            sourceDecoderProvider = remember(photoInfo.original) {
                {
                    val url = photoInfo.original
                    commonViewModel.prepareGetFile(url) { bytesSentTotal, contentLength ->
                        Log.d(TAG, "Downloading($bytesSentTotal/$contentLength) $url")
                    }.let { path ->
                        photoPath = path
                        DefaultImageBitmapRegionDecoderFactory(path)
                    }
                }
            },
            previewProvider = remember(photoInfo.thumb) {
                {
                    commonViewModel.getImageBitmap(photoInfo.thumb)
                }
            },
            sourceIntSize = IntSize(photoInfo.width, photoInfo.height),
            modifier = Modifier.fillMaxSize()
        ) { loadEvent ->
            when (loadEvent) {
                SubsamplingState.LoadEvent.Loading -> {
                    Log.d(TAG, "loadEvent:[$page] Loading for $photoInfo")
                    showLoading = true
                }

                SubsamplingState.LoadEvent.PreviewLoaded -> {
                    Log.d(TAG, "loadEvent:[$page] $loadEvent for $photoInfo")
                    showLoading = false
                }

                SubsamplingState.LoadEvent.SourceLoaded -> {
                    Log.d(TAG, "loadEvent:[$page] $loadEvent for $photoInfo")
                    showLoading = false
                }

                is SubsamplingState.LoadEvent.PreviewLoadError -> {
                    Log.e(TAG, "loadEvent:[$page] PreviewLoadError for $photoInfo", loadEvent.e)
                }

                is SubsamplingState.LoadEvent.SourceLoadError -> {
                    Log.e(TAG, "loadEvent:[$page] SourceLoadError for $photoInfo", loadEvent.e)
                    showLoading = false
                    showFailure = true
                    photoPath?.let { FileSystem.SYSTEM.delete(it) }
                }

                is SubsamplingState.LoadEvent.TileLoadError -> {
                    Log.e(TAG, "loadEvent:[$page] TileLoadError for $photoInfo", loadEvent.e)
                }

                is SubsamplingState.LoadEvent.DisposePreview -> {
                    Log.d(TAG, "loadEvent:[$page] DisposePreview for $photoInfo")
                    loadEvent.preview.recycle()
                }

                SubsamplingState.LoadEvent.Destroyed -> {
                    Log.d(TAG, "loadEvent:[$page] Destroyed for $photoInfo")
                    photoPath?.let { FileSystem.SYSTEM.delete(it) }
                }
            }
        }
    }
}
