package com.hym.zhankumultiplatform.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.hym.zhankumultiplatform.compose.decodeToImageBitmap
import com.hym.zhankumultiplatform.di.GlobalComponent
import com.hym.zhankumultiplatform.network.prepareGetBytes
import com.hym.zhankumultiplatform.network.use
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
import okio.SYSTEM
import okio.buffer
import okio.use

/**
 * @author hehua2008
 * @date 2024/5/12
 */
class CommonViewModel(private val httpClient: HttpClient = GlobalComponent.Instance.httpClient) :
    ViewModel() {
    suspend fun getImageBitmap(url: String): ImageBitmap {
        httpClient.get(url).use { response ->
            val byteArray: ByteArray = response.body()
            return byteArray.decodeToImageBitmap()
        }
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

        httpClient.get(url) {
            onDownload { bytesSentTotal, contentLength ->
                onProgress?.invoke(bytesSentTotal, contentLength)
            }
        }.use { response ->
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
