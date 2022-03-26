package com.hym.logcollector.util

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.DatabaseUtils
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.content.FileProvider
import java.io.*
import java.text.DecimalFormat

/**
 * @author hehua2008
 * @date 2021/8/24
 */
object FileUtils {
    /**
     * TAG for log messages.
     */
    private const val TAG = "FileUtils"
    private const val DEBUG = false // Set to true to enable logging

    private const val DOCUMENTS_DIR = "documents"

    // configured android:authorities in AndroidManifest
    // (https://developer.android.com/reference/android/support/v4/content/FileProvider)
    private const val AUTHORITY = "YOUR_AUTHORITY.provider"
    private const val HIDDEN_PREFIX = "."

    /**
     * File and folder comparator. TODO Expose sorting option method
     */
    @SuppressLint("DefaultLocale")
    val comparator = java.util.Comparator { f1: File, f2: File ->
        f1.name.toLowerCase().compareTo(f2.name.toLowerCase())
    }

    /**
     * File (not directories) filter.
     */
    val fileFilter = FileFilter { file: File ->
        file.isFile && !file.name.startsWith(HIDDEN_PREFIX)
    }

    /**
     * Folder (directories) filter.
     */
    val dirFilter = FileFilter { file: File ->
        file.isDirectory && !file.name.startsWith(HIDDEN_PREFIX)
    }

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension; null if uri was null.
     */
    fun getExtension(uri: String): String {
        val dot = uri.lastIndexOf(".")
        return if (dot >= 0) {
            uri.substring(dot)
        } else {
            // No extension.
            ""
        }
    }

    /**
     * @return Whether the URI is a local one.
     */
    fun isLocal(url: String): Boolean {
        return !url.startsWith("http://") && !url.startsWith("https://")
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    fun isMediaUri(uri: Uri): Boolean {
        return "media".equals(uri.authority, ignoreCase = true)
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    fun getUri(file: File): Uri {
        return Uri.fromFile(file)
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    fun getPathWithoutFilename(file: File): File {
        return if (file.isDirectory) {
            // no file to be split off. Return everything
            file
        } else {
            val filename = file.name
            val filepath = file.absolutePath

            // Construct path without file name.
            var pathwithoutname = filepath.substring(0, filepath.length - filename.length)
            if (pathwithoutname.endsWith("/")) {
                pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length - 1)
            }
            File(pathwithoutname)
        }
    }

    /**
     * @return The MIME type for the given file.
     */
    fun getMimeType(file: File): String? {
        val extension = getExtension(file.name)
        return if (extension.isEmpty()) "application/octet-stream"
        else MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1))
    }

    /**
     * @return The MIME type for the give Uri.
     */
    fun getMimeType(context: Context, uri: Uri): String? {
        val file = File(getPath(context, uri))
        return getMimeType(file)
    }

    /**
     * @return The MIME type for the give String Uri.
     */
    fun getMimeType(context: Context, url: String): String {
        return context.contentResolver.getType(Uri.parse(url)) ?: "application/octet-stream"
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is local.
     */
    fun isLocalStorageDocument(uri: Uri): Boolean {
        return AUTHORITY == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }

    fun isGoogleDriveUri(uri: Uri): Boolean {
        return "com.google.android.apps.docs.storage.legacy" == uri.authority
                || "com.google.android.apps.docs.storage" == uri.authority
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris, and other
     * file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    fun getDataColumn(
        context: Context, uri: Uri, selection: String?, selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = MediaStore.Files.FileColumns.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri, projection, selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG) {
                    DatabaseUtils.dumpCursor(cursor)
                }
                val columnIndex = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }
        return null
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access Framework
     * Documents, as well as the _data field for the MediaStore and other file-based
     * ContentProviders.<br></br>
     * <br></br>
     * Callers should check whether the path is local before assuming it represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @see .isLocal
     * @see .getFile
     */
    fun getPath(context: Context, uri: Uri): String {
        val absolutePath = getLocalPath(context, uri)
        return absolutePath ?: uri.toString()
    }

    private fun getLocalPath(context: Context, uri: Uri): String? {
        if (DEBUG) {
            Log.d(
                "$TAG File -",
                "Authority: " + uri.authority +
                        ", Fragment: " + uri.fragment +
                        ", Port: " + uri.port +
                        ", Query: " + uri.query +
                        ", Scheme: " + uri.scheme +
                        ", Host: " + uri.host +
                        ", Segments: " + uri.pathSegments.toString()
            )
        }
        @SuppressLint("ObsoleteSdkInt")
        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri)
            } else if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if ("home".equals(type, ignoreCase = true)) {
                    return Environment.getExternalStorageDirectory().toString() +
                            "/documents/" + split[1]
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                if (id != null && id.startsWith("raw:")) {
                    return id.substring(4)
                }
                val contentUriPrefixesToTry = arrayOf(
                    "content://downloads/public_downloads",
                    "content://downloads/my_downloads"
                )
                for (contentUriPrefix in contentUriPrefixesToTry) {
                    val contentUri = ContentUris.withAppendedId(
                        Uri.parse(contentUriPrefix),
                        id.toLong()
                    )
                    try {
                        val path = getDataColumn(context, contentUri, null, null)
                        if (path != null) {
                            return path
                        }
                    } catch (e: Exception) {
                    }
                }

                // path could not be retrieved using ContentResolver, therefore copy file to
                // accessible cache using streams
                val fileName = getFileName(context, uri)
                val file = fileName?.let {
                    val cacheDir = getDocumentCacheDir(context)
                    generateFileName(it, cacheDir)
                }
                var destinationPath: String? = null
                if (file != null) {
                    destinationPath = file.absolutePath
                    saveFileFromUri(context, uri, destinationPath)
                }
                return destinationPath
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                when (type) {
                    "image" -> {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    }
                    "video" -> {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    }
                    "audio" -> {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                }
                contentUri ?: return null
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri, selection, selectionArgs)
            } else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context)
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return uri.lastPathSegment
            } else if (isGoogleDriveUri(uri)) {
                return getGoogleDriveFilePath(uri, context)
            }
            if (isHuaWeiUri(uri)) {
                val uriPath = uri.path
                //content://com.huawei.hidisk.fileprovider/root/storage/emulated/0/Android/data
                // /com.xxx.xxx/
                if (uriPath != null && uriPath.startsWith("/root")) {
                    return uriPath.replace("/root".toRegex(), "")
                }
            }
            return getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            return uri.path
        }
        return null
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the Uri is unsupported or
     * pointed to a remote resource.
     * @author paulburke
     * @see .getPath
     */
    fun getFile(context: Context, uri: Uri): File? {
        val path = getPath(context, uri)
        return if (isLocal(path)) File(path) else null
    }

    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    fun getReadableFileSize(size: Int): String {
        val BYTES_IN_KILOBYTES = 1024
        val dec = DecimalFormat("###.#")
        var fileSize = 0f
        var suffix = " KB"
        if (size > BYTES_IN_KILOBYTES) {
            fileSize = (size / BYTES_IN_KILOBYTES).toFloat()
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize /= BYTES_IN_KILOBYTES
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize /= BYTES_IN_KILOBYTES
                    suffix = " GB"
                } else {
                    suffix = " MB"
                }
            }
        }
        return dec.format(fileSize.toDouble()) + suffix
    }

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     */
    fun createGetContentIntent(): Intent {
        // Implicitly allow the user to select a particular kind of data
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        // The MIME data type filter
        intent.type = "*/*"
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        return intent
    }

    /**
     * Creates View intent for given file
     *
     * @param file
     * @return The intent for viewing file
     */
    fun getViewIntent(context: Context, file: File): Intent {
        //Uri uri = Uri.fromFile(file);
        val uri = FileProvider.getUriForFile(context, AUTHORITY, file)
        val intent = Intent(Intent.ACTION_VIEW)
        val url = file.toString()
        when {
            url.contains(".doc") || url.contains(".docx") -> {
                // Word document
                intent.setDataAndType(uri, "application/msword")
            }
            url.contains(".pdf") -> {
                // PDF file
                intent.setDataAndType(uri, "application/pdf")
            }
            url.contains(".ppt") || url.contains(".pptx") -> {
                // Powerpoint file
                intent.setDataAndType(uri, "application/vnd.ms-powerpoint")
            }
            url.contains(".xls") || url.contains(".xlsx") -> {
                // Excel file
                intent.setDataAndType(uri, "application/vnd.ms-excel")
            }
            url.contains(".zip") || url.contains(".rar") -> {
                // WAV audio file
                intent.setDataAndType(uri, "application/x-wav")
            }
            url.contains(".rtf") -> {
                // RTF file
                intent.setDataAndType(uri, "application/rtf")
            }
            url.contains(".wav") || url.contains(".mp3") -> {
                // WAV audio file
                intent.setDataAndType(uri, "audio/x-wav")
            }
            url.contains(".gif") -> {
                // GIF file
                intent.setDataAndType(uri, "image/gif")
            }
            url.contains(".jpg") || url.contains(".jpeg") || url.contains(".png") -> {
                // JPG file
                intent.setDataAndType(uri, "image/jpeg")
            }
            url.contains(".txt") -> {
                // Text file
                intent.setDataAndType(uri, "text/plain")
            }
            url.contains(".3gp") || url.contains(".mpg") || url.contains(".mpeg") ||
                    url.contains(".mpe") || url.contains(".mp4") || url.contains(".avi") -> {
                // Video files
                intent.setDataAndType(uri, "video/*")
            }
            else -> {
                intent.setDataAndType(uri, "*/*")
            }
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        return intent
    }

    val downloadsDir: File =
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

    fun getDocumentCacheDir(context: Context): File {
        val dir = File(context.cacheDir, DOCUMENTS_DIR)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        logDir(context.cacheDir)
        logDir(dir)
        return dir
    }

    private fun logDir(dir: File) {
        if (!DEBUG) return
        Log.d(TAG, "Dir=$dir")
        val files = dir.listFiles()
        files?.forEach {
            Log.d(TAG, "File=" + it.path)
        }
    }

    fun generateFileName(name: String, directory: File): File? {
        var file = File(directory, name)
        if (file.exists()) {
            var fileName: String = name
            var extension = ""
            val dotIndex = name.lastIndexOf('.')
            if (dotIndex > 0) {
                fileName = name.substring(0, dotIndex)
                extension = name.substring(dotIndex)
            }
            var index = 0
            while (file.exists()) {
                index++
                file = File(directory, "$fileName($index)$extension")
            }
        }
        try {
            if (!file.createNewFile()) {
                return null
            }
        } catch (e: IOException) {
            Log.w(TAG, e)
            return null
        }
        logDir(directory)
        return file
    }

    private fun saveFileFromUri(context: Context, uri: Uri, destinationPath: String) {
        var ins: InputStream? = null
        var bos: BufferedOutputStream? = null
        try {
            ins = context.contentResolver.openInputStream(uri) ?: return
            bos = BufferedOutputStream(FileOutputStream(destinationPath, false))
            val buf = ByteArray(1024)
            ins.read(buf)
            do {
                bos.write(buf)
            } while (ins.read(buf) != -1)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                ins?.close()
                bos?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun readBytesFromFile(filePath: String): ByteArray? {
        var fileInputStream: FileInputStream? = null
        var bytesArray: ByteArray? = null
        try {
            val file = File(filePath)
            bytesArray = ByteArray(file.length().toInt())

            //read file into bytes[]
            fileInputStream = FileInputStream(file)
            fileInputStream.read(bytesArray)
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return bytesArray
    }

    @Throws(IOException::class)
    fun createTempImageFile(context: Context, fileName: String): File {
        // Create an image file name
        val storageDir = File(context.cacheDir, DOCUMENTS_DIR)
        return File.createTempFile(fileName, ".jpg", storageDir)
    }

    fun getFileName(context: Context, uri: Uri): String? {
        val mimeType = context.contentResolver.getType(uri)
        var filename: String? = null
        if (mimeType == null) {
            val path = getPath(context, uri)
            filename = File(path).name
        } else {
            val returnCursor = context.contentResolver.query(
                uri, null, null, null, null
            )
            if (returnCursor != null) {
                val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                returnCursor.moveToFirst()
                filename = returnCursor.getString(nameIndex)
                returnCursor.close()
            }
        }
        return filename
    }

    fun getName(filename: String): String {
        val index = filename.lastIndexOf('/')
        return filename.substring(index + 1)
    }

    private fun getGoogleDriveFilePath(uri: Uri, context: Context): String? {
        val returnCursor = context.contentResolver.query(uri, null, null, null, null) ?: return null
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        val name = returnCursor.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
            it.moveToFirst()
            it.getString(nameIndex)
        }
        val file = File(context.cacheDir, name)
        try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val outputStream = FileOutputStream(file)
            var read = 0
            val maxBufferSize = 1 * 1024 * 1024
            val bytesAvailable = inputStream!!.available()
            val bufferSize = bytesAvailable.coerceAtMost(maxBufferSize)
            val buffers = ByteArray(bufferSize)
            while (inputStream.read(buffers).also { read = it } != -1) {
                outputStream.write(buffers, 0, read)
            }
            inputStream.close()
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return file.path
    }

    fun isHuaWeiUri(uri: Uri): Boolean {
        return "com.huawei.hidisk.fileprovider" == uri.authority
    }
}