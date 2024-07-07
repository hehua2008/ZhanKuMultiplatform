package com.hym.zhankumultiplatform.ui.imagepager

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.IntSize
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.ImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.compose.LocalPlatformContext
import coil3.decode.DecodeResult
import coil3.decode.Decoder
import coil3.disk.DiskCache
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import com.hym.compose.subsamplingimage.DefaultImageBitmapRegionDecoderFactory
import com.hym.compose.subsamplingimage.SubsamplingImage
import com.hym.compose.subsamplingimage.SubsamplingState
import com.hym.compose.zoom.rememberZoomState
import com.hym.zhankumultiplatform.compose.EmptyCoilImage
import com.hym.zhankumultiplatform.compose.toImageBitmap
import com.hym.zhankumultiplatform.photo.UrlPhotoInfo
import com.hym.zhankumultiplatform.ui.CommonViewModel
import com.hym.zhankumultiplatform.util.Logger
import kotlinx.collections.immutable.ImmutableList
import org.jetbrains.compose.resources.vectorResource
import zhankumultiplatform.composeapp.generated.resources.Res
import zhankumultiplatform.composeapp.generated.resources.vector_image
import zhankumultiplatform.composeapp.generated.resources.vector_image_broken

/**
 * @author hehua2008
 * @date 2024/4/2
 */
private const val TAG = "ImageHorizontalPager"

@OptIn(ExperimentalFoundationApi::class, ExperimentalCoilApi::class)
@Composable
fun ImageHorizontalPager(
    commonViewModel: CommonViewModel = viewModel { CommonViewModel() },
    photoInfoList: ImmutableList<UrlPhotoInfo>,
    modifier: Modifier = Modifier,
    initialIndex: Int = 0,
    beyondBoundsPageCount: Int = 1,
    pagerState: PagerState = rememberPagerState(initialPage = initialIndex) { photoInfoList.size }
) {
    val loadingPainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_image)
    )
    val failurePainter = rememberVectorPainter(
        vectorResource(Res.drawable.vector_image_broken)
    )

    val platformContext = LocalPlatformContext.current
    val imageLoader = ImageLoader(platformContext)

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
        //var photoPath by remember { mutableStateOf<Path?>(null) }
        var originalSnapshot by remember { mutableStateOf<DiskCache.Snapshot?>(null) }

        SubsamplingImage(
            zoomState = zoomState,
            sourceDecoderProvider = remember(photoInfo.original) {
                {
                    /*
                    val url = photoInfo.original
                    commonViewModel.prepareGetFile(url) { bytesSentTotal, contentLength ->
                        Logger.d(TAG, "Downloading($bytesSentTotal/$contentLength) $url")
                    }.let { path ->
                        photoPath = path
                        DefaultImageBitmapRegionDecoderFactory(path)
                    }
                    */

                    // To preload a network image only into the disk cache
                    val imageRequest = ImageRequest.Builder(platformContext)
                        .data(photoInfo.original)
                        // Disable reading from/writing to the memory cache.
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        // Set a custom `Decoder.Factory` that skips the decoding step.
                        .decoderFactory { _, _, _ ->
                            Decoder { DecodeResult(EmptyCoilImage, false) }
                        }
                        .build()
                    val imageResult = imageLoader.execute(imageRequest)
                    val snapshot = imageLoader.diskCache!!
                        .openSnapshot(photoInfo.original)!!
                    originalSnapshot = snapshot
                    DefaultImageBitmapRegionDecoderFactory(snapshot.data)
                }
            },
            previewProvider = remember(photoInfo.thumb) {
                {
                    //commonViewModel.getImageBitmap(photoInfo.thumb)

                    // To preload an image into memory, enqueue or execute an ImageRequest without a Target
                    val request = ImageRequest.Builder(platformContext)
                        .data(photoInfo.thumb)
                        // Optional, but setting a ViewSizeResolver will conserve memory by limiting the size the image should be preloaded into memory at.
                        //.size(ViewSizeResolver(imageView))
                        .build()
                    val imageResult = imageLoader.execute(request)
                    imageResult.image!!.toImageBitmap()
                }
            },
            sourceIntSize = IntSize(photoInfo.width, photoInfo.height),
            modifier = Modifier
                .fillMaxSize()
                .background(color = if (isSystemInDarkTheme()) Color.Black else Color.White)
        ) { loadEvent ->
            when (loadEvent) {
                SubsamplingState.LoadEvent.Loading -> {
                    Logger.d(TAG, "loadEvent:[$page] Loading for $photoInfo")
                    showLoading = true
                }

                SubsamplingState.LoadEvent.PreviewLoaded -> {
                    Logger.d(TAG, "loadEvent:[$page] $loadEvent for $photoInfo")
                    showLoading = false
                }

                SubsamplingState.LoadEvent.SourceLoaded -> {
                    Logger.d(TAG, "loadEvent:[$page] $loadEvent for $photoInfo")
                    showLoading = false
                }

                is SubsamplingState.LoadEvent.PreviewLoadError -> {
                    Logger.e(TAG, "loadEvent:[$page] PreviewLoadError for $photoInfo", loadEvent.e)
                }

                is SubsamplingState.LoadEvent.SourceLoadError -> {
                    Logger.e(TAG, "loadEvent:[$page] SourceLoadError for $photoInfo", loadEvent.e)
                    showLoading = false
                    showFailure = true
                    //photoPath?.let { FileSystem.SYSTEM.delete(it) }
                    originalSnapshot?.close()
                }

                is SubsamplingState.LoadEvent.TileLoadError -> {
                    Logger.e(TAG, "loadEvent:[$page] TileLoadError for $photoInfo", loadEvent.e)
                }

                is SubsamplingState.LoadEvent.DisposePreview -> {
                    Logger.d(TAG, "loadEvent:[$page] DisposePreview for $photoInfo")
                    //loadEvent.preview.recycle()
                }

                SubsamplingState.LoadEvent.Destroyed -> {
                    Logger.d(TAG, "loadEvent:[$page] Destroyed for $photoInfo")
                    //photoPath?.let { FileSystem.SYSTEM.delete(it) }
                    originalSnapshot?.close()
                }
            }
        }
    }
}
