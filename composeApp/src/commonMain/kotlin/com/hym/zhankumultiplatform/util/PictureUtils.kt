package com.hym.zhankumultiplatform.util

import androidx.compose.material3.SnackbarHostState

expect object PictureUtils {
    fun download(snackbarHostState: SnackbarHostState? = null, imgUrls: List<String>)

    fun download(snackbarHostState: SnackbarHostState? = null, vararg imgUrls: String)

    suspend fun coroutineDownload(
        snackbarHostState: SnackbarHostState? = null,
        imgUrls: List<String>
    ): List<String>

    suspend fun coroutineDownload(
        snackbarHostState: SnackbarHostState? = null,
        vararg imgUrls: String
    ): List<String>
}
