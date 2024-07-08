package com.hym.zhankumultiplatform.compose

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import coil3.BitmapImage
import coil3.Canvas
import coil3.annotation.ExperimentalCoilApi

/**
 * @author hehua2008
 * @date 2024/4/26
 */
@OptIn(ExperimentalCoilApi::class)
actual object EmptyCoilImage : coil3.Image {
    override val size: Long = 0
    override val width: Int = 0
    override val height: Int = 0
    override val shareable: Boolean = true

    override fun draw(canvas: Canvas) {
        // draw nothing
    }
}

actual fun ByteArray.decodeToImageBitmap(): ImageBitmap {
    val bitmap = BitmapFactory.decodeByteArray(this, 0, size)
    return bitmap.asImageBitmap()
}

@OptIn(ExperimentalCoilApi::class)
actual fun coil3.Image.toImageBitmap(): ImageBitmap {
    if (this is BitmapImage) {
        return this.bitmap.asImageBitmap()
    } else {
        throw UnsupportedOperationException("$this")
    }
}
