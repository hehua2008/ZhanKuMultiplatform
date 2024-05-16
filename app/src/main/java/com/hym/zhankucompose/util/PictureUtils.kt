package com.hym.zhankucompose.util

import android.content.ContentValues
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import com.hym.zhankucompose.MyApplication
import com.hym.zhankucompose.hilt.NetworkModule
import com.hym.zhankucompose.network.prepareGetFile
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.FileSystem
import okio.Path
import okio.Sink
import okio.Source
import okio.buffer
import okio.sink
import java.io.File
import java.io.IOException

object PictureUtils {
    private val TAG = "PictureUtils"
    private val DIR_NAME = "ZhanKu"

    fun download(snackbarHostState: SnackbarHostState? = null, imgUrls: List<String>) {
        if (imgUrls.isEmpty()) return
        download(snackbarHostState, *imgUrls.toTypedArray())
    }

    fun download(snackbarHostState: SnackbarHostState? = null, vararg imgUrls: String) {
        if (imgUrls.isEmpty()) return
        GlobalScope.launch {
            coroutineDownload(snackbarHostState, *imgUrls)
        }
    }

    suspend fun coroutineDownload(
        snackbarHostState: SnackbarHostState? = null,
        imgUrls: List<String>
    ): List<String> {
        if (imgUrls.isEmpty()) return emptyList()
        return coroutineDownload(snackbarHostState, *imgUrls.toTypedArray())
    }

    suspend fun coroutineDownload(
        snackbarHostState: SnackbarHostState? = null,
        vararg imgUrls: String
    ): List<String> {
        if (imgUrls.isEmpty()) return emptyList()
        return withContext(Dispatchers.Main) {
            val context = MyApplication.INSTANCE
            val accessor =
                EntryPointAccessors.fromApplication(context, NetworkModule.Accessor::class.java)
            val httpClient = accessor.httpClient()
            val imgFiles = mutableListOf<Path>()
            val startMsg = "Start to download ${imgUrls.size} images"
            Log.d(TAG, startMsg)
            snackbarHostState?.showSnackbar(message = startMsg)
            val failedUrls = mutableListOf<String>()
            imgUrls.forEachIndexed { index, url ->
                val deferred = async(Dispatchers.IO) {
                    val name = Uri.parse(url).lastPathSegment?.let {
                        if (it.lastIndexOf('.') == -1) "$it.jpg" else it
                    } ?: "${System.currentTimeMillis()}.jpg"

                    val values = ContentValues()
                    values.put(MediaStore.Images.Media.DESCRIPTION, "This is an image")
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, name)
                    when {
                        name.endsWith(".png") -> {
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                            values.put(MediaStore.Images.Media.TITLE, "Image.png")
                        }

                        name.endsWith(".gif") -> {
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif")
                            values.put(MediaStore.Images.Media.TITLE, "Image.gif")
                        }

                        else -> {
                            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            values.put(MediaStore.Images.Media.TITLE, "Image.jpg")
                        }
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.put(
                            MediaStore.Images.Media.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES + File.separatorChar + DIR_NAME
                        )
                    }
                    val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    val resolver = context.contentResolver
                    val insertUri = resolver.insert(externalContentUri, values)
                    if (insertUri == null) {
                        Log.e(TAG, "insert uri for $values failed")
                        return@async null
                    }
                    val src = try {
                        val path = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / name
                        httpClient.prepareGetFile(url, path)
                        path
                    } catch (e: Exception) {
                        Log.e(TAG, "download $url failed", e)
                        return@async null
                    }
                    var source: Source? = null
                    var sink: Sink? = null
                    try {
                        val output = resolver.openOutputStream(insertUri)
                        if (output == null) {
                            Log.e(TAG, "openOutputStream for $insertUri failed")
                            return@async null
                        }
                        sink = output.sink().buffer()
                        source = FileSystem.SYSTEM.source(src).buffer()
                        do {
                            val count = source.read(sink.buffer, 8192)
                        } while (count > 0)
                        return@async src
                    } catch (e: IOException) {
                        Log.e(TAG, "copy $src to $insertUri failed", e)
                        return@async null
                    } finally {
                        sink?.close()
                        source?.close()
                    }
                }

                deferred.await().let {
                    if (it != null) {
                        imgFiles.add(it)
                    } else {
                        failedUrls.add(url)
                        val failedMsg = "Failed to download ${index + 1})/${imgUrls.size}: $url"
                        Log.w(TAG, failedMsg)
                        snackbarHostState?.showSnackbar(
                            message = failedMsg, duration = SnackbarDuration.Long
                        )
                    }
                }
            }

            val completeMsg = "Saved ${imgFiles.size} images"
            Log.d(TAG, completeMsg)
            snackbarHostState?.showSnackbar(message = completeMsg, duration = SnackbarDuration.Long)
            failedUrls
        }
    }
}
