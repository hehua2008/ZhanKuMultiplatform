package com.hym.zhankumultiplatform.network

import com.hym.zhankumultiplatform.util.Logger
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicLong

class LogInterceptor : Interceptor {
    companion object {
        private const val TAG = "LogInterceptor"
    }

    private val mCount = AtomicLong()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val count = mCount.incrementAndGet()
        val request = chain.request()
        Logger.d(TAG, "[$count] >>>>>> $request")
        val response = chain.proceed(request)
        Logger.d(TAG, "[$count] <<<<<< $response contentLength=${response.body?.contentLength()}")
        return response
    }
}