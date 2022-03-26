package com.hym.logcollector.ui

import android.os.Handler
import android.os.HandlerThread
import android.text.SpannableString
import android.text.Spanned
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hym.logcollector.LogcatServiceImpl
import com.hym.logcollector.base.LogLevel
import com.hym.logcollector.impl.*
import com.hym.logcollector.util.LOG_KEYWORD_STYLES
import com.hym.logcollector.util.wrapper
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * @author hehua2008
 * @date 2021/8/21
 */
internal class LogcatViewModel : ViewModel(), LogcatServiceImpl.LogUpdatedListener {
    @Volatile
    var logcatService: LogcatServiceImpl? = null
        set(value) {
            if (field === value) return
            field?.removeLogUpdateListener(this)
            field = value
            value?.addLogUpdateListener(this)
        }

    @Volatile
    var logTypeIndex = 0
        private set

    @Volatile
    private var mKeyword = ""

    private var mLogPattern: Pattern? = null

    @Volatile
    private var mLogLevel = LogLevel.DEFAULT

    private val _toastMsg = MutableLiveData<String>()
    val toastMsg: LiveData<String> = _toastMsg

    private val _logList = MutableLiveData<List<LogWrapper>>()
    val logList: LiveData<List<LogWrapper>> = _logList

    private val mWorkHandler = HandlerThread("logcat-view-model-work-thread").run {
        start()
        Handler(looper)
    }

    private val mUpdatePattern: Runnable = object : Runnable {
        override fun run() {
            mWorkHandler.removeCallbacks(this)
            val keyword = mKeyword
            mLogPattern = try {
                if (keyword.isBlank()) null
                else Pattern.compile("(?i)($keyword)")
            } catch (e: PatternSyntaxException) {
                _toastMsg.postValue(e.message)
                null
            }
            mUpdateLogList.run()
        }
    }

    private val mClearLog = object : Runnable {
        override fun run() {
            mWorkHandler.removeCallbacks(this)
            logcatService?.clearLog(logTypeIndex)
            _logList.postValue(emptyList())
        }
    }

    private val mUpdateLogList = object : Runnable {
        override fun run() {
            mWorkHandler.removeCallbacks(this)
            val newLogList = logcatService?.run { logLists[logTypeIndex].toMutableList() } ?: return
            val pattern = mLogPattern
            val logLevel = mLogLevel
            if (pattern == null && logLevel == LogLevel.DEFAULT) {
                _logList.postValue(newLogList)
                return
            }
            val iterator = newLogList.listIterator()
            while (iterator.hasNext()) {
                val next = iterator.next()
                if (next.logLevel.level < logLevel.level) {
                    iterator.remove()
                    continue
                }
                pattern ?: continue
                var spannable: SpannableString? = null
                val m = pattern.matcher(next)
                while (m.find()) {
                    if (spannable == null) spannable = SpannableString(next)
                    LOG_KEYWORD_STYLES.forEach {
                        spannable.setSpan(
                            it.wrapper(), m.start(), m.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                }
                if (spannable != null) {
                    iterator.set(LogWrapper.wrap(next.lineNumber, next.logLevel, spannable))
                } else {
                    iterator.remove()
                }
            }
            _logList.postValue(newLogList)
        }
    }

    override fun onLogUpdated(logTypeIndex: Int) {
        if (logTypeIndex == this@LogcatViewModel.logTypeIndex) mWorkHandler.post(mUpdateLogList)
    }

    fun setLogType(index: Int) {
        if (logTypeIndex == index) return
        logTypeIndex = index
        mWorkHandler.post(mUpdatePattern)
    }

    fun setLogLevel(index: Int) {
        val logLevel = LogLevel.values()[index]
        if (mLogLevel == logLevel) return
        mLogLevel = logLevel
        mWorkHandler.post(mUpdateLogList)
    }

    fun setKeyword(keyword: String) {
        val newKeyword = keyword.trim()
        if (mKeyword == newKeyword) return
        mKeyword = newKeyword
        mWorkHandler.post(mUpdatePattern)
    }

    fun clearLog() {
        mWorkHandler.post(mClearLog)
    }

    override fun onCleared() {
        logcatService = null
        mWorkHandler.looper.quit()
    }
}