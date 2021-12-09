package com.hym.zhankukotlin.network

import okhttp3.Headers
import okhttp3.Headers.Companion.toHeaders

object Constants {
    const val HOST = "www.zcool.com.cn"
    const val API_HOST = "api.zcool.com.cn"
    const val API_URL = "https://api.zcool.com.cn/v2/api/"
    val BASE_HEADERS: Headers

    init {
        val headerMap: MutableMap<String, String> = mutableMapOf()
        headerMap["Accept-Language"] = "zh-cn"
        ("{\"uniqueCode\":\"07841fde-9dcf-40f1-aa53-595032b62a5c\"," +
                "\"appId\":\"com.zcool.community\"," +
                "\"channel\":\"oppo\"," +
                "\"mobileType\":\"android\"," +
                "\"versionCode\":4644}").let {
            headerMap["common"] = it
            headerMap["BaseInfo"] = it
        }
        BASE_HEADERS = headerMap.toHeaders()
    }
}