package com.hym.zhankucompose.network

import android.util.Log
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.internal.closeQuietly
import java.io.IOException
import java.net.HttpURLConnection.HTTP_GATEWAY_TIMEOUT
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit

class ImageInterceptor : Interceptor {
    companion object {
        private const val TAG = "ImageInterceptor"
    }

    private val mSemaphores = ConcurrentHashMap<String, Semaphore>()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.body != null || request.method != "GET") {
            return chain.proceed(request)
        }

        val url = request.url.toString()
        val newSemaphore = Semaphore(0)
        val preSemaphore = mSemaphores.putIfAbsent(url, newSemaphore)
        if (preSemaphore == null) { // The first call
            return try {
                chain.proceed(request)
            } finally {
                mSemaphores.remove(url)
                newSemaphore.release(Int.MAX_VALUE)
            }
        } else { // The second and subsequent calls
            try {
                Log.d(TAG, "Start wait for the previous request to finish for $url")
                if (!preSemaphore.tryAcquire(5000, TimeUnit.MILLISECONDS)) {
                    Log.w(TAG, "Wait timeout for the previous request to finish for $url")
                }
            } catch (ignore: InterruptedException) {
            }

            val cacheRequest = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build()
            for (count in 1..40) { // Try to get cache response written by the first call
                val cacheResponse = chain.proceed(cacheRequest)
                if (cacheResponse.code == HTTP_GATEWAY_TIMEOUT) {
                    cacheResponse.body?.closeQuietly()
                    try {
                        Thread.sleep(50)
                    } catch (ignore: InterruptedException) {
                    }
                    continue
                }
                Log.d(TAG, "Hit cache response after trying $count times for $url")
                return cacheResponse
            }

            Log.d(TAG, "Proceed normal request for $url")
            return chain.proceed(request)
        }
    }
}
