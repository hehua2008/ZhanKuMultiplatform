package com.hym.logcollector.impl

import android.util.Log
import com.hym.logcollector.base.LogConfig
import com.hym.logcollector.base.parseLineNoThrow
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.concurrent.CopyOnWriteArraySet
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author hehua2008
 * @date 2021/8/17
 */
internal class LogCollector(private val mLogConfig: LogConfig) {
    companion object {
        private const val TAG = "LogCollector"
        private const val THREAD_NAME = TAG

        private const val DATE_TIME_PATTERN = "MM-dd HH:mm:ss.SSS"
        private const val DATE_TIME_LENGTH = DATE_TIME_PATTERN.length
        private val DATE_TIME_FORMAT = SimpleDateFormat(DATE_TIME_PATTERN)
        private val DATE_TIME_REGEX = Regex("^\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}\\.\\d{3}")

        private const val STATUS_READY = 0
        private const val STATUS_STARTED = 1
        private const val STATUS_STOPPED = 2
    }

    interface OnStopCallback {
        /**
         * @return true to indicate that it should restart, otherwise it should terminate
         */
        fun onStop(isInterrupted: Boolean): Boolean
    }

    private val mSingleThreadPool = Executors.newSingleThreadExecutor {
        object : Thread(it) {
            init {
                name = "$THREAD_NAME-$id"
            }

            override fun run() {
                Log.d(TAG, "$name started !")
                super.run()
                Log.d(TAG, "$name stopped !")
            }
        }
    }

    private val mStatus = AtomicInteger(STATUS_READY)
    private val mLineNumber = AtomicInteger(0)
    private var mLastLogLine: String? = null
    private val mListeners = CopyOnWriteArraySet<LogChangedListener>()

    @Suppress("ComplexMethod")
    fun start(onStopCallback: OnStopCallback? = null) {
        if (!mStatus.compareAndSet(STATUS_READY, STATUS_STARTED)) {
            val e = IllegalStateException("$this is not in ready status: ${mStatus.get()}")
            Log.w(TAG, "start failed ", e)
        }

        mSingleThreadPool.submit {
            //val tname = Thread.currentThread().name
            var isInterrupted = false

            try {
                // Avoid frequent restart of the logcat process
                TimeUnit.SECONDS.sleep(if (mLineNumber.get() == 0) 1 else 10)

                val specifiedTime = mLastLogLine?.substring(0, DATE_TIME_LENGTH)
                val process = Runtime.getRuntime().exec(
                    listOfNotNull(
                        "logcat",
                        "-v",
                        "threadtime",
                        if (specifiedTime != null) "-T" else null,
                        specifiedTime
                    ).toTypedArray()
                )
                BufferedReader(InputStreamReader(process.inputStream)).use { reader ->
                    var previousLogLine = mLastLogLine
                    val logLines = mutableListOf<String>()
                    val logWrappers = mutableListOf<LogWrapper>()

                    while (true) {
                        if (Thread.interrupted()) {
                            isInterrupted = true
                            break
                        }
                        do {
                            val logLine = reader.readLine()
                            if (logLine == null) {
                                //Log.w(TAG, "$tname reached eol !")
                                process.destroy()
                                return@use
                            }
                            logLines.add(logLine)
                        } while (reader.ready())

                        // exclusive
                        val toIndex = logLines.size
                        // inclusive
                        val fromIndex = previousLogLine?.let {
                            val firstLine = logLines.firstOrNull { line ->
                                DATE_TIME_REGEX.find(line) != null
                            } ?: return@let null
                            previousLogLine = null
                            val firstTime =
                                DATE_TIME_FORMAT.parse(firstLine.substring(0, DATE_TIME_LENGTH))!!
                            val previousTime =
                                DATE_TIME_FORMAT.parse(it.substring(0, DATE_TIME_LENGTH))!!
                            if (previousTime < firstTime) return@let null
                            val previousIndex = logLines.indexOf(it)
                            if (previousIndex >= 0) previousIndex + 1 else null
                        } ?: 0

                        if (fromIndex >= toIndex) {
                            logLines.clear()
                            continue
                        }

                        logLines.subList(fromIndex, toIndex).mapTo(logWrappers) {
                            val lineNumber = mLineNumber.getAndIncrement()
                            val (level, line) = mLogConfig.logcatParser.parseLineNoThrow(it)
                            LogWrapper.wrap(lineNumber, level, line)
                        }

                        for (l in mListeners) {
                            val addedLogList = logWrappers.filter {
                                l.filter(it)
                            }
                            l.onLogChanged(addedLogList)
                        }

                        logLines.lastOrNull {
                            DATE_TIME_REGEX.find(it) != null
                        }?.let {
                            mLastLogLine = it
                        }
                        logLines.clear()
                        logWrappers.clear()
                    }
                }
            } catch (e: InterruptedException) {
                isInterrupted = true
            } catch (e: IOException) {
                Log.e(TAG, "logcollector failed !", e)
            } finally {
                if (mStatus.compareAndSet(STATUS_STARTED, STATUS_READY)
                    && onStopCallback?.onStop(isInterrupted) == true
                ) {
                    start(onStopCallback) // restart
                } else {
                    stop()
                }
            }
        }
    }

    fun stop() {
        mStatus.set(STATUS_STOPPED)
        mSingleThreadPool.shutdownNow()
        mListeners.clear()
    }

    fun addLogChangedListener(listener: LogChangedListener) {
        mListeners.add(listener)
    }

    fun removeLogChangedListener(listener: LogChangedListener) {
        mListeners.remove(listener)
    }

    interface LogChangedListener {
        fun filter(logWrapper: LogWrapper): Boolean

        fun onLogChanged(addedLogList: List<LogWrapper>)
    }
}