package com.hym.logcollector.ui

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.*
import com.hym.logcollector.R
import com.hym.logcollector.base.LogConfig
import com.hym.logcollector.base.LogLevel
import com.hym.logcollector.base.LogParser
import com.hym.logcollector.impl.*
import com.hym.logcollector.util.CharsetList
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.io.IOException
import java.nio.charset.Charset
import java.util.concurrent.atomic.AtomicReference
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

/**
 * @author hehua2008
 * @date 2021/8/22
 */
internal class LogPagingViewModel(private val mLogConfig: LogConfig) : ViewModel() {
    companion object {
        const val TAG = "LogPagingViewModel"
    }

    private val mLoadingLogFileReader = AtomicReference<TextFileReader?>(null)
    private val mLogFileReader = MutableLiveData<TextFileReader?>()

    private var mLogDecoder: Charset = CharsetList[0]

    private val mLogFileParser = MutableLiveData<LogParser<*>>(LogParser.DEFAULT.ORIGINAL)

    private val mLogLevel = MutableLiveData(LogLevel.DEFAULT)

    private val mLogPattern = MutableLiveData<Pattern?>()

    private val _loadingProgress = MutableLiveData<Float>()
    val loadingProgress: LiveData<Float> = _loadingProgress

    private val _toastMsg = MutableLiveData<Pair<Int, Array<Any?>?>>()
    val toastMsg: LiveData<Pair<Int, Array<Any?>?>> = _toastMsg

    val refresh: LiveData<Unit> = MutableLiveData<Unit>()
        .apply {
            mLogFileReader.observeForever {
                value = Unit
            }
            mLogFileParser.observeForever {
                mLogFileReader.value ?: return@observeForever
                value = Unit
            }
            mLogLevel.observeForever {
                mLogFileReader.value ?: return@observeForever
                value = Unit
            }
            mLogPattern.observeForever {
                if (mLogFileReader.value == null)
                    _toastMsg.postValue(R.string.log_toast_wait_log_file_load to null)
                else value = Unit
            }
        }

    val pagingFlow: Flow<PagingData<LogWrapper>> =
        Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            config = PagingConfig(pageSize = mLogConfig.logFilePageSize),
            initialKey = 0
        ) {
            LogPagingSource(
                mLogFileReader.value,
                mLogFileParser.value ?: LogParser.DEFAULT.ORIGINAL,
                mLogLevel.value ?: LogLevel.DEFAULT,
                mLogPattern.value,
                mLogConfig.logFilePageSize
            )
        }
            .flow
            // Loads and transformations before the cachedIn operation will be cached, so that
            // multiple observers get the same data. This is true either for simultaneous observers.
            .cachedIn(viewModelScope)

    fun setLogFile(logFile: File) {
        startLoadingLogFile(logFile)
    }

    fun setLogFileParser(index: Int) {
        mLogFileParser.value = mLogConfig.logFileParsers[index]
    }

    fun setLogLevel(index: Int) {
        mLogLevel.value = LogLevel.values()[index]
    }

    fun setLogDecoder(index: Int) {
        mLogDecoder = CharsetList[index]
        startLoadingLogFile()
    }

    private fun startLoadingLogFile(file: File? = null) {
        val logFile = file ?: (mLoadingLogFileReader.get() ?: mLogFileReader.value)?.file ?: return
        val charset = mLogDecoder
        viewModelScope.launch(Dispatchers.IO) {
            TextFileReader(logFile, charset).apply {
                mLoadingLogFileReader.getAndSet(this)?.cancelInit()
                initLinePositions { progress, cancelled ->
                    if (cancelled) {
                        if (mLoadingLogFileReader.get() == null) { // Don't post progress if has new
                            _loadingProgress.postValue(progress)
                        }
                        cancel()
                    } else {
                        _loadingProgress.postValue(progress)
                    }
                }
                if (!isActive) return@launch // Job is cancelled
                if (!mLoadingLogFileReader.compareAndSet(this, null)) {
                    // cancelLoadingLogFile() was called
                    return@launch
                }
                closeLogFile()
                mLogFileReader.postValue(this)
            }
        }
    }

    fun cancelLoadingLogFile() {
        mLoadingLogFileReader.getAndSet(null)?.cancelInit()
    }

    fun setKeyword(keyword: String) {
        val newKeyword = keyword.trim()
        viewModelScope.launch(Dispatchers.Default) {
            val pattern = try {
                if (newKeyword.isBlank()) null
                else Pattern.compile("(?i)($newKeyword)")
            } catch (e: PatternSyntaxException) {
                _toastMsg.postValue(R.string.log_toast_regex_error to arrayOf(e.message))
                null
            }
            if (mLogPattern.value == pattern) return@launch
            mLogPattern.postValue(pattern)
        }
    }

    private fun closeLogFile() {
        try {
            mLogFileReader.value?.close()
        } catch (e: IOException) {
            Log.e(TAG, "closeLogFile failed !", e)
        }
    }

    override fun onCleared() {
        closeLogFile()
    }
}