package com.hym.logcollector.base

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * e.g. LogcatType("SystemErr", "System\\.err")
 *
 * @author hehua2008
 * @date 2021/8/20
 */
@Parcelize
data class LogcatType @JvmOverloads constructor(
    val name: String,
    val regex: String?,
    val cacheLines: Int = DEFAULT_CACHE_LINES
) : Parcelable {
    companion object {
        const val DEFAULT_CACHE_LINES = 3000

        @JvmField
        val ALL = LogcatType("ALL", null)
    }
}