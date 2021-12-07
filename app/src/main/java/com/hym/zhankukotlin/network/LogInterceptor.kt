package com.hym.zhankukotlin.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.atomic.AtomicLong

class LogInterceptor : Interceptor {
    private val mCount = AtomicLong()

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val count = mCount.incrementAndGet()
        val request = chain.request()
        Log.d(TAG, "[$count] >>>>>> $request")
        val response = chain.proceed(request)
        Log.d(TAG, "[$count] <<<<<< $response contentLength=${response.body?.contentLength()}")
        return response
    }

    companion object {
        private val TAG = LogInterceptor::class.simpleName
    }
}