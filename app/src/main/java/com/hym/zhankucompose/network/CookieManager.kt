package com.hym.zhankucompose.network

import android.app.Application
import android.util.Log
import com.hym.zhankucompose.util.HexDump
import com.hym.zhankucompose.util.StringUtils
import me.tatarka.inject.annotations.Inject
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.io.*
import java.util.concurrent.ConcurrentHashMap

@Inject
class CookieManager(private val mContext: Application) : CookieJar {
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        val host = url.toUrl().host
        val cookieSerialize = CookieSerialize(cookies.toMutableList())
        COOKIE_MAP[host] = cookieSerialize
        val fileName = hostStringToFileName(host)
        val dir = File(mContext.cacheDir, COOKIE_DIR_NAME)
        if (!dir.isDirectory) {
            dir.mkdirs()
        }
        val file = File(dir, fileName)
        file.delete()
        try {
            ObjectOutputStream(FileOutputStream(file)).use { out -> out.writeObject(cookieSerialize) }
        } catch (e: IOException) {
            Log.w(TAG, "saveFromResponse failed", e)
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val host = url.toUrl().host
        val cookieSerialize = COOKIE_MAP[host]
        if (cookieSerialize != null) {
            return cookieSerialize.mCookies
        } else {
            val fileName = hostStringToFileName(host)
            val dir = File(mContext.cacheDir, COOKIE_DIR_NAME)
            val file = File(dir, fileName)
            if (file.isFile) {
                try {
                    ObjectInputStream(FileInputStream(file)).use { input ->
                        return (input.readObject() as CookieSerialize).let {
                            COOKIE_MAP[host] = it
                            it.mCookies
                        }
                    }
                } catch (e: IOException) {
                    Log.w(TAG, "loadForRequest failed", e)
                } catch (e: ClassNotFoundException) {
                    Log.w(TAG, "loadForRequest failed", e)
                }
            }
        }
        return emptyList()
    }

    private class CookieSerialize(var mCookies: MutableList<Cookie>) : Serializable {
        @Throws(IOException::class)
        private fun writeObject(out: ObjectOutputStream) {
            val size = mCookies.size
            out.writeInt(size)
            for (cookie in mCookies) {
                out.writeObject(cookie.name)
                out.writeObject(cookie.value)
                out.writeLong(cookie.expiresAt)
                out.writeObject(cookie.domain)
                out.writeBoolean(cookie.hostOnly)
                out.writeObject(cookie.path)
                out.writeBoolean(cookie.secure)
                out.writeBoolean(cookie.httpOnly)
            }
        }

        @Throws(IOException::class, ClassNotFoundException::class)
        private fun readObject(input: ObjectInputStream) {
            val size = input.readInt()
            mCookies = ArrayList(size)
            for (i in 0 until size) {
                val name = input.readObject() as String
                val value = input.readObject() as String
                val expiresAt = input.readLong()
                val domain = input.readObject() as String
                val hostOnly = input.readBoolean()
                val path = input.readObject() as String
                val secure = input.readBoolean()
                val httpOnly = input.readBoolean()
                val builder = Cookie.Builder()
                    .name(name)
                    .value(value)
                    .expiresAt(expiresAt)
                    .path(path)
                if (hostOnly) {
                    builder.hostOnlyDomain(domain)
                } else {
                    builder.domain(domain)
                }
                if (secure) {
                    builder.secure()
                }
                if (httpOnly) {
                    builder.httpOnly()
                }
                mCookies.add(builder.build())
            }
        }

        companion object {
            private const val serialVersionUID = 6213134943587632458L
        }
    }

    companion object {
        private const val TAG = "CookieManager"
        private val COOKIE_MAP: MutableMap<String, CookieSerialize?> = ConcurrentHashMap()
        private const val COOKIE_DIR_NAME = "cookies"

        fun hostStringToFileName(hostStr: String): String {
            return HexDump.toHexString(hostStr.toByteArray())
        }

        fun fileNameToHostString(fileName: String): String? {
            val len = fileName.length
            if (len and 1 != 0) return null
            val bytes = ByteArray(len / 2)
            var i = 0
            while (i < len) {
                bytes[i / 2] = fileName.substring(i, i + 2).toInt(16).toByte()
                i += 2
            }
            return StringUtils.newStringFromBytes(bytes)
        }
    }
}