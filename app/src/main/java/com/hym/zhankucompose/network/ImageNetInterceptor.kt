package com.hym.zhankucompose.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.EMPTY_RESPONSE
import okhttp3.internal.http.RealInterceptorChain
import java.io.IOException
import java.net.HttpURLConnection.HTTP_NOT_MODIFIED

class ImageNetInterceptor : Interceptor {
    companion object {
        private const val TAG = "ImageNetInterceptor"
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (request.body != null || request.method != "GET") {
            return chain.proceed(request)
        }

        if (request.header("If-Modified-Since") != null || request.header("If-None-Match") != null) {
            // java.lang.IllegalStateException: network interceptor must call proceed() exactly once
            RealInterceptorChain::class.java.getDeclaredField("calls").run {
                isAccessible = true
                set(chain, 1)
            }
            Log.d(TAG, "Respond HTTP Not Modified for $request")
            return respondNotModified(request)
        }

        val response = chain.proceed(request)
        return if (response.header("Cache-Control") != null) {
            response
        } else {
            response.newBuilder().header("Cache-Control", "max-age=60").build()
        }
    }

    private fun respondNotModified(request: Request): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(HTTP_NOT_MODIFIED)
            .message("Not Modified")
            .body(EMPTY_RESPONSE)
            .sentRequestAtMillis(-1L)
            .receivedResponseAtMillis(System.currentTimeMillis())
            .build()
    }
}
