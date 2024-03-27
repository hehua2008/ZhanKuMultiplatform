package com.hym.zhankucompose.network

import android.util.Log
import retrofit2.Converter
import java.io.IOException

/**
 * @author hehua2008
 * @date 2024/3/27
 */
class ConverterLogger<F, T>(private val delegated: Converter<F, T>) : Converter<F, T> {
    companion object {
        private const val TAG = "ConverterLogger"
    }

    private val delegatedTag = "${delegated.javaClass.simpleName}@${delegated.hashCode()}"

    @Throws(IOException::class)
    override fun convert(source: F): T? {
        return try {
            delegated.convert(source)
        } catch (e: Exception) {
            Log.w(TAG, "$delegatedTag convert failed", e)
            throw e
        }
    }
}

fun <F, T> Converter<F, T>.withLogger(): Converter<F, T> {
    return ConverterLogger(this)
}
