package com.hym.zhankukotlin.util

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.MyApplication
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

object PictureUtils {
    private const val TAG = "PictureUtils"

    @JvmStatic
    fun download(imgUrls: List<String>) {
        download(*imgUrls.toTypedArray())
    }

    @JvmStatic
    fun download(vararg imgUrls: String) {
        if (imgUrls.isEmpty()) return
        GlobalScope.launch(Dispatchers.Main) {
            val context = MyApplication.INSTANCE
            val imgFiles = mutableListOf<File>()
            val startMsg = "Start to download ${imgUrls.size} images"
            Log.d(TAG, startMsg)
            Toast.makeText(context, startMsg, Toast.LENGTH_SHORT).show()
            imgUrls.forEachIndexed { index, url ->
                val futureTarget = GlideApp.with(context)
                    .download(url)
                    .submit()

                val deferred = async(Dispatchers.IO) {
                    val name = Uri.parse(url).lastPathSegment?.let {
                        if (it.lastIndexOf('.') == -1) "$it.jpg" else it
                    } ?: "${System.currentTimeMillis()}.jpg"

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                        var dir = Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES
                        )
                        if (!dir.isDirectory && !dir.mkdirs()) {
                            Log.e(TAG, "getExternalStoragePublicDirectory failed")
                            dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                            if (dir == null) {
                                Log.e(TAG, "getExternalFilesDir failed")
                                return@async null
                            }
                        }
                        val src = try {
                            futureTarget.get()
                        } catch (e: Exception) {
                            Log.e(TAG, "download $url failed", e)
                            return@async null
                        }
                        val dst = File(dir, name)
                        try {
                            src.copyTo(dst, true)
                            try {
                                MediaStore.Images.Media.insertImage(
                                    context.contentResolver, dst.absolutePath, name, null
                                )
                            } catch (e: IOException) {
                                Log.e(TAG, "insert $dst to media library failed", e)
                                return@async null
                            }
                            context.sendBroadcast(
                                Intent(
                                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
                                    Uri.parse(dst.absolutePath)
                                )
                            )
                            return@async dst
                        } catch (e: IOException) {
                            Log.e(TAG, "copy $src to $dst failed", e)
                            return@async null
                        }
                    } else {
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
                        values.put(
                            MediaStore.Images.Media.RELATIVE_PATH,
                            Environment.DIRECTORY_PICTURES
                        )
                        val externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                        val resolver = context.contentResolver
                        val insertUri = resolver.insert(externalContentUri, values)
                        if (insertUri == null) {
                            Log.e(TAG, "insert uri for $values failed")
                            return@async null
                        }
                        val src = try {
                            futureTarget.get()
                        } catch (e: Exception) {
                            Log.e(TAG, "download $url failed", e)
                            return@async null
                        }
                        var output: OutputStream? = null
                        var input: InputStream? = null
                        try {
                            output = resolver.openOutputStream(insertUri)
                            if (output == null) {
                                Log.e(TAG, "openOutputStream for $insertUri failed")
                                return@async null
                            }
                            input = src.inputStream()
                            input.copyTo(output)
                            return@async src
                        } catch (e: IOException) {
                            Log.e(TAG, "copy $src to $insertUri failed", e)
                            return@async null
                        } finally {
                            output?.close()
                            input?.close()
                        }
                    }
                }

                deferred.await().let {
                    if (it != null) {
                        imgFiles.add(it)
                    } else {
                        val failedMsg = "Failed to download ${index + 1})/${imgUrls.size}: $url"
                        Log.w(TAG, failedMsg)
                        Toast.makeText(context, failedMsg, Toast.LENGTH_SHORT).show()
                    }
                }
            }

            val completeMsg = "Saved ${imgFiles.size} images"
            Log.d(TAG, completeMsg)
            Toast.makeText(context, completeMsg, Toast.LENGTH_LONG).show()
        }
    }
}