package com.hym.zhankucompose.network

import com.hym.zhankucompose.model.Cate
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

object NetworkConstants {
    const val USER_AGENT =
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.0.1 Safari/605.1.15"
    const val HOST = "www.zcool.com.cn"
    const val API_HOST = "api.zcool.com.cn"
    const val API_URL = "https://api.zcool.com.cn/v2/api/"

    val JsonDefault = Json {
        ignoreUnknownKeys = true

        SerializersModule {
            polymorphic(Cate::class) {
                subclass(TopCate::class, TopCate)
                subclass(SubCate::class, SubCate)
            }
        }
    }
}
