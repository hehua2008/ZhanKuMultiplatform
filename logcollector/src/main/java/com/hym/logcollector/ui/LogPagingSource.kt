package com.hym.logcollector.ui

import android.text.SpannableString
import android.text.Spanned
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hym.logcollector.base.LogLevel
import com.hym.logcollector.base.LogParser
import com.hym.logcollector.base.parseLineNoThrow
import com.hym.logcollector.impl.*
import com.hym.logcollector.util.LOG_KEYWORD_STYLES
import com.hym.logcollector.util.wrapper
import kotlinx.coroutines.*
import java.io.IOException
import java.util.regex.Pattern

/**
 * @author hehua2008
 * @date 2021/8/22
 */
internal class LogPagingSource(
    private val mLogFileReader: TextFileReader?,
    private val mLogFileParser: LogParser<*>,
    private val mLogLevel: LogLevel,
    private val mLogPattern: Pattern?,
    private val mLogPageSize: Int
) : PagingSource<Int, LogWrapper>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LogWrapper> {
        try {
            // Start refresh at line 1 if undefined.
            val nextPageStartLineNumber = params.key ?: 1 // include
            val nextPageEndLineNumber = nextPageStartLineNumber + params.loadSize // exclude
            val logList = ArrayList<LogWrapper>(params.loadSize)
            mLogFileReader ?: return LoadResult.Page(logList, null, null)
            withContext(Dispatchers.IO) {
                synchronized(mLogFileReader) {
                    mLogFileReader.seekToLine(nextPageStartLineNumber)
                    var currentLineNumber = nextPageStartLineNumber - 1
                    while (true) {
                        if (++currentLineNumber == nextPageEndLineNumber) break
                        if (!isActive) {
                            cancel()
                            return@withContext
                        }
                        val line = mLogFileReader.readLine() ?: break
                        val pair = mLogFileParser.parseLineNoThrow(line)
                        val logLevel = pair.first
                        if (logLevel.level < mLogLevel.level) continue
                        var logLine = pair.second
                        if (mLogPattern != null) {
                            var spannable: SpannableString? = null
                            val m = mLogPattern.matcher(logLine)
                            while (m.find()) {
                                if (spannable == null) spannable = SpannableString(logLine)
                                LOG_KEYWORD_STYLES.forEach {
                                    spannable.setSpan(
                                        it.wrapper(), m.start(), m.end(),
                                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                                    )
                                }
                            }
                            logLine = spannable ?: continue
                        }
                        val logWrapper = LogWrapper.wrap(currentLineNumber, logLevel, logLine)
                        logList.add(logWrapper)
                    }
                }
            }
            val prevKey = (nextPageStartLineNumber - mLogPageSize).let {
                when {
                    it >= 1 -> it
                    (nextPageStartLineNumber > 1) -> 1
                    else -> null
                }
            }
            val nextKey = (nextPageEndLineNumber).let {
                if (it > mLogFileReader.lineCount) null else it
            }
            return LoadResult.Page(logList, prevKey, nextKey)
        } catch (e: IOException) {
            // IOException for read file failures.
            return LoadResult.Error(e)
        } catch (e: Exception) {
            // Handle errors in this block and return LoadResult.Error if it is an expected error.
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, LogWrapper>): Int? {
        // Try to find the page key of the closest page to anchorPosition, from either the prevKey
        // or the nextKey, but you need to handle nullability here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it) ?: return@let null
            anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
        }
    }
}
