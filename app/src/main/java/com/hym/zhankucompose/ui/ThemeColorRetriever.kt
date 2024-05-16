package com.hym.zhankucompose.ui

import androidx.compose.ui.graphics.ImageBitmap
import coil3.SingletonImageLoader
import coil3.annotation.ExperimentalCoilApi
import coil3.request.ImageRequest
import com.hym.zhankucompose.MyApplication
import com.hym.zhankucompose.compose.toImageBitmap
import com.hym.zhankucompose.util.MMCQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ThemeColorRetriever {
    private val imageLoader by lazy { SingletonImageLoader.get(MyApplication.INSTANCE) }

    @OptIn(ExperimentalCoilApi::class)
    suspend fun getMainThemeColor(model: Any): MMCQ.ThemeColor? {
        val imageRequest = ImageRequest.Builder(MyApplication.INSTANCE)
            .data(model)
            .size(100)
            .build()
        val imageResult = imageLoader.execute(imageRequest)
        val image = imageResult.image ?: return null
        val imageBitmap = image.toImageBitmap()
        return getMainThemeColor(imageBitmap)
    }

    suspend fun getMainThemeColor(bitmap: ImageBitmap): MMCQ.ThemeColor? {
        val themeColors = withContext(Dispatchers.Default) {
            val mmcq = MMCQ(bitmap, 3)
            mmcq.quantize()
        }
        return if (themeColors.isEmpty()) null else themeColors[0]
    }
}
