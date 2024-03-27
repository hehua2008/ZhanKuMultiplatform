package com.hym.zhankucompose.network

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * @author hehua2008
 * @date 2024/3/27
 */
class ConverterFactoryDelegate(private val delegated: Converter.Factory) : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        return delegated.responseBodyConverter(type, annotations, retrofit)?.withLogger()
    }

    override fun requestBodyConverter(
        type: Type,
        parameterAnnotations: Array<out Annotation>,
        methodAnnotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, RequestBody>? {
        return delegated.requestBodyConverter(
            type, parameterAnnotations, methodAnnotations, retrofit
        )?.withLogger()
    }

    override fun stringConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<*, String>? {
        return delegated.stringConverter(type, annotations, retrofit)?.withLogger()
    }
}
