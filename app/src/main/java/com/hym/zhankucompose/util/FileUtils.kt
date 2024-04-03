package com.hym.zhankucompose.util

import android.media.MediaMetadataRetriever
import android.util.Log
import java.io.File

/**
 * @author hehua2008
 * @date 2024/4/2
 */
private const val TAG = "FileUtils"

private val ExtractExtensionFromMimeType = Regex("^\\w+/.*?(\\w+)$")

fun String.getMimeType(): String? {
    MediaMetadataRetriever().use { mediaMetadataRetriever ->
        try {
            mediaMetadataRetriever.setDataSource(this)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "getMimeType failed for $this", e)
            return null
        }
        return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE)
    }
}

fun File.getMimeType(): String? {
    return this.absolutePath.getMimeType()
}

fun String.mimeTypeToExtension(): String? {
    return ExtractExtensionFromMimeType.find(this)?.groupValues?.getOrNull(1)
}
