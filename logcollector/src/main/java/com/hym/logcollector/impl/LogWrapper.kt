package com.hym.logcollector.impl

import android.text.Spanned
import com.hym.logcollector.base.LogLevel

/**
 * @author hehua2008
 * @date 2021/8/17
 */
interface LogWrapper : CharSequence {
    companion object {
        @JvmStatic
        fun wrap(lineNumber: Int, logLevel: LogLevel, logLine: CharSequence): LogWrapper {
            return if (logLine is Spanned) SpannedLogWrapper(lineNumber, logLevel, logLine)
            else CharSequenceLogWrapper(lineNumber, logLevel, logLine)
        }
    }

    val lineNumber: Int

    val logLevel: LogLevel
}

private open class CharSequenceLogWrapper(
    override val lineNumber: Int,
    override val logLevel: LogLevel,
    private val logLine: CharSequence
) : LogWrapper, CharSequence by logLine {
    override fun hashCode() = lineNumber

    override fun equals(other: Any?): Boolean {
        if (other !is CharSequenceLogWrapper) return false
        if (this === other) return true
        return logLine == other.logLine
    }

    override fun toString() = logLine.toString()
}

private class SpannedLogWrapper(
    lineNumber: Int,
    logLevel: LogLevel,
    private val spannedLogLine: Spanned
) : CharSequenceLogWrapper(lineNumber, logLevel, spannedLogLine), Spanned by spannedLogLine {
    override fun hashCode() = super.hashCode()

    override fun equals(other: Any?): Boolean {
        if (other !is SpannedLogWrapper) return false
        if (this === other) return true
        return spannedLogLine == other.spannedLogLine
    }
}
