package com.hym.zhankucompose.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.hym.zhankucompose.compose.decodeToImageBitmap
import com.hym.zhankucompose.network.prepareGetBytes
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.onDownload
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.util.date.getTimeMillis
import io.ktor.utils.io.readAvailable
import okio.ByteString.Companion.encodeUtf8
import okio.FileSystem
import okio.Path
import okio.buffer
import okio.use
import javax.inject.Inject

/**
 * @author hehua2008
 * @date 2024/5/12
 */
@HiltViewModel
class CommonViewModel @Inject constructor(private val httpClient: HttpClient) :
    ViewModel() {
    suspend fun getImageBitmap(url: String): ImageBitmap {
        val response = httpClient.get(url)
        val byteArray: ByteArray = response.body()
        return byteArray.decodeToImageBitmap()
    }

    suspend fun getFile(
        url: String,
        onProgress: ((bytesSentTotal: Long, contentLength: Long?) -> Unit)? = null
    ): Path {
        val md5 = url.encodeUtf8().md5().hex().uppercase()
        val dataFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "ktor_$md5.data"
        val metadata = FileSystem.SYSTEM.metadataOrNull(dataFile)
        val size = metadata?.size
        if (size != null && size > 0 && metadata.isRegularFile) {
            onProgress?.invoke(size, size)
            return dataFile
        }
        //FileSystem.SYSTEM.delete(dataFile)

        val response = httpClient.get(url) {
            onDownload { bytesSentTotal, contentLength ->
                onProgress?.invoke(bytesSentTotal, contentLength)
            }
        }
        val bodyChannel = response.bodyAsChannel()

        val now = getTimeMillis()
        val tmpFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "ktor_$md5-$now.tmp"
        //FileSystem.SYSTEM.delete(tmpFile)

        FileSystem.SYSTEM.sink(tmpFile, true).buffer().use { bufferedSink ->
            val bytes = ByteArray(8192)
            while (true) {
                val count = bodyChannel.readAvailable(bytes)
                if (count <= 0) break
                bufferedSink.write(bytes, 0, count)
            }
            bufferedSink.flush()
        }

        // "ktor_$md5-$now.tmp"" -> "ktor_$md5.data"
        FileSystem.SYSTEM.atomicMove(tmpFile, dataFile)
        return dataFile
    }

    suspend fun prepareGetFile(
        url: String,
        onProgress: ((bytesSentTotal: Long, contentLength: Long?) -> Unit)? = null
    ): Path {
        val md5 = url.encodeUtf8().md5().hex().uppercase()
        val dataFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "ktor_$md5.data"
        val metadata = FileSystem.SYSTEM.metadataOrNull(dataFile)
        val size = metadata?.size
        if (size != null && size > 0 && metadata.isRegularFile) {
            onProgress?.invoke(size, size)
            return dataFile
        }
        //FileSystem.SYSTEM.delete(dataFile)

        val now = getTimeMillis()
        val tmpFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "ktor_$md5-$now.tmp"
        //FileSystem.SYSTEM.delete(tmpFile)

        FileSystem.SYSTEM.sink(tmpFile, true).buffer().use { bufferedSink ->
            httpClient.prepareGetBytes(url = url, onProgress = onProgress) { bytes, count ->
                bufferedSink.write(bytes, 0, count)
            }
            bufferedSink.flush()
        }

        // "ktor_$md5-$now.tmp"" -> "ktor_$md5.data"
        FileSystem.SYSTEM.atomicMove(tmpFile, dataFile)
        return dataFile
    }
}
