package com.hym.zhankumultiplatform.util

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format.FormatStringsInDatetimeFormats
import kotlinx.datetime.format.byUnicodePattern
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime

/**
 * @author hehua2008
 * @date 2022/3/28
 */

const val ONE_MINUTE_MILLIS = 60 * 1000
const val TEN_MINUTE_MILLIS = 10 * ONE_MINUTE_MILLIS
const val ONE_HOUR_MILLIS = 60 * ONE_MINUTE_MILLIS
const val ONE_DAY_MILLIS = 24 * ONE_HOUR_MILLIS

const val DateTimePattern = "yyyy-MM-dd HH:mm:ss"
val DateTimeRegex = Regex("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}")

@OptIn(FormatStringsInDatetimeFormats::class)
val DateTimeFormat = LocalDateTime.Format {
    byUnicodePattern(DateTimePattern)
}

fun Long.toDateString(): String {
    val instant = Instant.fromEpochMilliseconds(this)
    val datetimeInSystemZone = instant.toLocalDateTime(TimeZone.currentSystemDefault())
    datetimeInSystemZone.run {
        return "$year.$monthNumber.$dayOfMonth"
    }
}

fun Long.getRelativeOrActualDateString(
    relativeTo: Long = Clock.System.now().toEpochMilliseconds()
): String {
    return (relativeTo - this).let {
        when {
            it < TEN_MINUTE_MILLIS -> "刚刚"
            it < ONE_HOUR_MILLIS -> "${it / ONE_MINUTE_MILLIS}分钟前"
            it < ONE_DAY_MILLIS -> "${it / ONE_HOUR_MILLIS}小时前"
            else -> {
                when (val dateDiff =
                    ((relativeTo / ONE_DAY_MILLIS) - (this / ONE_DAY_MILLIS)).toInt()) {
                    0 -> "今天"
                    1 -> "昨天"
                    2 -> "前天"
                    3, 4, 5, 6, 7 -> "${dateDiff}天前"
                    else -> toDateString()
                }
            }
        }
    }
}

fun String.getDateTime(index: Int = 0): Long? {
    var count = 0
    var m: MatchResult? = DateTimeRegex.find(this)
    while (m != null) {
        if (count++ == index) {
            val localDateTime = DateTimeFormat.parse(m.value)
            return localDateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        }
        m = m.next()
    }
    return null
}
