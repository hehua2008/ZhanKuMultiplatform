package com.hym.logcollector.base

import android.graphics.Typeface
import android.os.Parcel
import android.os.Parcelable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.hym.logcollector.util.wrapper
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * @author hehua2008
 * @date 2021/8/20
 */
@Suppress("SwallowedException")
fun LogParser<*>.parseLineNoThrow(line: String): Pair<LogLevel, CharSequence> {
    val pair = try {
        parseLine(line)
    } catch (e: Exception) {
        Pair(LogLevel.DEFAULT, line)
    }
    return if (pair.second.length <= LogParser.MAX_LENGTH) pair
    else Pair(pair.first, pair.second.subSequence(0, LogParser.MAX_LENGTH))
}

/**
 * In addition to implementing this LogParser interface, the implementation class must also be an
 * enumeration class and implement the Parcelable interface.
 * When using kotlin version below kotlin 1.5, please manually implement the Parcelable interface,
 * do not use the @Parcelize annotation, because the generated code has bugs !!!
 */
interface LogParser<E> : Parcelable where E : Enum<E>, E : LogParser<E> {
    companion object {
        const val DATE_REGEX = "\\d{2}-\\d{2}"
        const val TIME_REGEX = "\\d{2}:\\d{2}:\\d{2}\\.\\d{3}"
        const val MAX_LENGTH = 10000

        val THREAD_TAG_STYLES: List<CharacterStyle> = listOf(StyleSpan(Typeface.BOLD))
    }

    val displayName: String

    @Throws(Exception::class)
    fun parseLine(line: String): Pair<LogLevel, CharSequence>

    enum class DEFAULT : LogParser<DEFAULT> {
        THREAD_TIME {
            private val mLogRegex: Pattern =
                Pattern.compile("($DATE_REGEX)\\s+($TIME_REGEX)\\s+(\\d+)\\s+(\\d+)\\s+(\\w)\\s+(.*?)(?:\\s*): (.*)")

            override val displayName: String = "LogcatDefault"

            override fun parseLine(line: String): Pair<LogLevel, CharSequence> {
                var logLine: CharSequence = line
                var logLevel = LogLevel.DEFAULT
                val m = mLogRegex.matcher(logLine)
                if (m.find() && m.groupCount() == 7) {
                    val date = m.group(1)!!
                    val time = m.group(2)!!
                    val process = m.group(3)!!
                    val thread = m.group(4)!!
                    val level = m.group(5)!!
                    val tag = m.group(6)!!
                    val msg = m.group(7)!!
                    logLevel = LogLevel.getLogLevel(level)
                    logLine = SpannableString("$date $time $process-$thread $tag: $msg").apply {
                        val start = date.length + 1 + time.length + 1
                        val end = start + process.length + 1 + thread.length + 1 + tag.length
                        THREAD_TAG_STYLES.forEach {
                            setSpan(it.wrapper(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        }
                        /*
                        val offset = end + 2
                        val urlM = PatternsCompat.WEB_URL.matcher(msg)
                        while (urlM.find()) {
                            setSpan(
                                URLSpan(urlM.group()),
                                offset + urlM.start(),
                                offset + urlM.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                            )
                        }
                        */
                    }
                }
                return logLevel to logLine
            }
        },
        LOGAN_INFO {
            private val mGson = Gson()
            private val mSimpleDateFormat: SimpleDateFormat = SimpleDateFormat("MM-dd HH:mm:ss.SSS")

            override val displayName: String = "LoganInfo"

            override fun parseLine(line: String): Pair<LogLevel, CharSequence> {
                val info = mGson.fromJson(line, ParseLoganInfo::class.java)
                val logLevel = LogLevel.getLogLevel(info.level)
                val time = mSimpleDateFormat.format(Date(info.timestamp))
                val logLine = SpannableString("$time ${info.threadName} ${info.content}").apply {
                    val start = time.length + 1
                    val end = start + info.threadName.length
                    THREAD_TAG_STYLES.forEach {
                        setSpan(it.wrapper(), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                    }
                    /*
                    val offset = end + 1
                    val urlM = PatternsCompat.WEB_URL.matcher(info.content)
                    while (urlM.find()) {
                        setSpan(
                            URLSpan(urlM.group()),
                            offset + urlM.start(),
                            offset + urlM.end(),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    */
                }
                return Pair(logLevel, logLine)
            }
        },
        ORIGINAL {
            override val displayName: String = "Original"

            override fun parseLine(line: String): Pair<LogLevel, CharSequence> {
                return Pair(LogLevel.DEFAULT, line)
            }
        };

        data class ParseLoganInfo(
            @SerializedName("c")
            val content: String = "",

            @SerializedName("f")
            val level: Int = 0,

            @SerializedName("l")
            val timestamp: Long = 0L,

            @SerializedName("n")
            val threadName: String = "",

            @SerializedName("i")
            val threadId: Int = 0,

            @SerializedName("m")
            val isInMainThread: Boolean = false
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(name)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<DEFAULT> {
            override fun createFromParcel(parcel: Parcel): DEFAULT {
                return valueOf(parcel.readString()!!)
            }

            override fun newArray(size: Int): Array<DEFAULT?> {
                return arrayOfNulls(size)
            }
        }
    }
}