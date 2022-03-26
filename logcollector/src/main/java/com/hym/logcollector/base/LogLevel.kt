package com.hym.logcollector.base

import android.util.Log

/**
 * @author hehua2008
 * @date 2021/8/24
 */
enum class LogLevel(val level: Int) {
    DEFAULT(0),
    VERBOSE(Log.VERBOSE),
    DEBUG(Log.DEBUG),
    INFO(Log.INFO),
    WARN(Log.WARN),
    ERROR(Log.ERROR);

    companion object {
        @JvmStatic
        fun getLogLevel(level: String?): LogLevel =
            when (level) {
                "V" -> VERBOSE
                "D" -> DEBUG
                "I" -> INFO
                "W" -> WARN
                "E" -> ERROR
                else -> DEFAULT
            }

        @JvmStatic
        fun getLogLevel(level: Int?): LogLevel =
            when (level) {
                Log.VERBOSE -> VERBOSE
                Log.DEBUG -> DEBUG
                Log.INFO -> INFO
                Log.WARN -> WARN
                Log.ERROR -> ERROR
                else -> DEFAULT
            }
    }
}