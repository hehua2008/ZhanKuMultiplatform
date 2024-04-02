package com.hym.zhankucompose.photo

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import kotlinx.parcelize.Parcelize
import java.io.File

/**
 * @author hehua2008
 * @date 2022/3/8
 */
sealed interface PhotoInfo<T : Any> {
    val original: T

    val thumb: T get() = original

    val description: String get() = original.toString()

    val width: Int get() = -1

    val height: Int get() = -1

    fun hasThumb() = original != thumb
}

@Parcelize
data class UrlPhotoInfo(
    override val original: String,
    override val thumb: String = original,
    override val description: String = original,
    override val width: Int = -1,
    override val height: Int = -1
) : PhotoInfo<String>, Parcelable

@Parcelize
data class UriPhotoInfo(
    override val original: Uri,
    override val thumb: Uri = original,
    override val description: String = original.toString(),
    override val width: Int = -1,
    override val height: Int = -1
) : PhotoInfo<Uri>, Parcelable

@Parcelize
data class FilePhotoInfo(
    override val original: File,
    override val thumb: File = original,
    override val description: String = original.toString(),
    override val width: Int = -1,
    override val height: Int = -1
) : PhotoInfo<File>, Parcelable

@Parcelize
data class ResPhotoInfo(
    @RawRes @DrawableRes override val original: Int,
    override val thumb: Int = original,
    override val description: String = original.toString(),
    override val width: Int = -1,
    override val height: Int = -1
) : PhotoInfo<Int>, Parcelable

data class BitmapPhotoInfo(
    override val original: Bitmap,
    override val thumb: Bitmap = original,
    override val description: String = "Bitmap(byteCount=${original.byteCount}, ${original.width}x${original.height})",
    override val width: Int = -1,
    override val height: Int = -1
) : PhotoInfo<Bitmap>

data class DrawablePhotoInfo(
    override val original: Drawable,
    override val thumb: Drawable = original,
    override val description: String = "${original.javaClass.simpleName}(${original.intrinsicWidth}x${original.intrinsicHeight})",
    override val width: Int = -1,
    override val height: Int = -1
) : PhotoInfo<Drawable>

data class ByteArrayPhotoInfo(
    override val original: ByteArray,
    override val thumb: ByteArray = original,
    override val description: String = "ByteArray(${original.size})",
    override val width: Int = -1,
    override val height: Int = -1
) : PhotoInfo<ByteArray> {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayPhotoInfo

        if (original !== other.original) return false
        if (thumb !== other.thumb) return false
        if (description != other.description) return false
        if (width != other.width) return false
        if (height != other.height) return false

        return true
    }

    override fun hashCode(): Int {
        var result = original.hashCode()
        result = 31 * result + thumb.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + width
        result = 31 * result + height
        return result
    }
}