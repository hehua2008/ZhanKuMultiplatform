package com.hym.logcollector.base

import android.graphics.Color
import android.os.Parcelable
import android.util.ArrayMap
import kotlinx.parcelize.Parcelize

/**
 * @author hehua2008
 * @date 2021/8/20
 */
@Parcelize
data class LogLevelColorMapper internal constructor(
    private val mMap: Map<LogLevel, Int>
) : Parcelable {
    companion object {
        val WARN_COLOR = Color.parseColor("#F78C6C")
        val ERROR_COLOR = Color.parseColor("#FF5370")

        val DEFAULT = LogLevelColorMapper {
            when (it) {
                LogLevel.WARN -> WARN_COLOR
                LogLevel.ERROR -> ERROR_COLOR
                else -> Color.BLACK
            }
        }
    }

    constructor(levelToColor: (LogLevel) -> Int) : this(
        ArrayMap<LogLevel, Int>().apply {
            LogLevel.values().forEach { put(it, levelToColor(it)) }
        }
    )

    fun map(logLevel: LogLevel): Int = mMap[logLevel]!!
}