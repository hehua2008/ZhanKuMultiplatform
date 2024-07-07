package com.hym.zhankumultiplatform.util

import androidx.compose.material3.SnackbarHostState

actual object PictureUtils {
    actual fun download(
        snackbarHostState: SnackbarHostState?,
        imgUrls: List<String>
    ) {
        // TODO
    }

    actual fun download(
        snackbarHostState: SnackbarHostState?,
        vararg imgUrls: String
    ) {
        // TODO
    }

    actual suspend fun coroutineDownload(
        snackbarHostState: SnackbarHostState?,
        imgUrls: List<String>
    ): List<String> {
        // TODO
        return emptyList()
    }

    actual suspend fun coroutineDownload(
        snackbarHostState: SnackbarHostState?,
        vararg imgUrls: String
    ): List<String> {
        // TODO
        return emptyList()
    }
}
