package com.hym.zhankucompose.model

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.TypeAdapterFactory
import com.google.gson.reflect.TypeToken

/**
 * @author hehua2008
 * @date 2021/12/8
 */
object ZkTypeAdapterFactory : TypeAdapterFactory {
    override fun <T> create(gson: Gson, type: TypeToken<T>): TypeAdapter<T>? {
        val typeAdapter = when {
            TopCate::class.java.isAssignableFrom(type.rawType) -> TopCate.TopCateTypeAdapter
            SubCate::class.java.isAssignableFrom(type.rawType) -> SubCate.SubCateTypeAdapter
            else -> null
        }
        return typeAdapter as? TypeAdapter<T>
    }
}