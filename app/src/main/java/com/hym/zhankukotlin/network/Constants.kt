package com.hym.zhankukotlin.network

import android.util.ArrayMap
import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders

object Constants {
    const val HOST = "www.zcool.com.cn"
    const val BASE_URL = "https://www.zcool.com.cn/"
    const val DISCOVER_URL = "https://www.zcool.com.cn/discover/"
    val BASE_HEADERS: Headers

    init {
        val headerMap: MutableMap<String, String> = ArrayMap(5)
        headerMap["User-Agent"] = ("Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7)"
                + " AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.1 Safari/605.1.15")
        headerMap["Accept"] = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"
        headerMap["Accept-Language"] = "zh-cn"
        BASE_HEADERS = headerMap.toHeaders()
    }
}