package com.hym.zhankukotlin.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HeaderInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        val host = request.url.host
        if (Constants.HOST == host) {
            val headers = request.headers.newBuilder().addAll(Constants.BASE_HEADERS).build()
            request = request.newBuilder().headers(headers).build()
        }
        return chain.proceed(request)
    }
}