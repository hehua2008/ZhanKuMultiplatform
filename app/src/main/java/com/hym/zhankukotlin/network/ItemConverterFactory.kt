package com.hym.zhankukotlin.network

import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type
import java.util.concurrent.ConcurrentHashMap

class ItemConverterFactory private constructor() : Converter.Factory() {
    private val mConverterMap: MutableMap<Type, ItemConverter<*>> = ConcurrentHashMap()

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *> {
        var converter = mConverterMap[type]
        if (converter === null) {
            converter = ItemConverter<Any>(type)
            mConverterMap[type] = converter
        }
        return converter
    }

    companion object {
        @JvmField
        val INSTANCE = ItemConverterFactory()
    }
}