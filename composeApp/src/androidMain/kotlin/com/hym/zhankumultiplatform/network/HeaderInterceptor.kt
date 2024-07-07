package com.hym.zhankumultiplatform.network

import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import java.util.concurrent.TimeUnit

class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val host = request.url.host
        val newRequest = request.newBuilder().run {
            if (request.body == null && request.method == "GET" && request.header("Cache-Control") == null) {
                cacheControl(CacheControl.Builder().maxAge(60, TimeUnit.SECONDS).build())
            }
            build()
        }
        return chain.proceed(newRequest)
    }
}