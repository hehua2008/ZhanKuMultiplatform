package com.hym.logcollector

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import com.hym.logcollector.base.LogConfig
import com.hym.logcollector.impl.LogCollector
import com.hym.logcollector.impl.LogWrapper
import com.hym.logcollector.util.NotificationUtils
import com.hym.logcollector.util.RingList
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import java.util.regex.Pattern
import java.util.regex.PatternSyntaxException

class LogcatService : Service() {
    companion object {
        private const val CHANNEL_ID = "LogcatService"

        const val LOG_CONFIG = "LOG_CONFIG"

        @JvmField
        var startLogConfig: LogConfig? = null // Save the initial default config

        @JvmStatic
        fun start(context: Context, logConfig: LogConfig) {
            val intent = Intent(context, LogcatService::class.java).putExtra(LOG_CONFIG, logConfig)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
    }

    private val binder = LogcatServiceImpl()

    override fun onCreate() {
        super.onCreate()
        showForegroundNotification(
            titleResId = R.string.notification_collecting,
            textResId = R.string.notification_foreground_text
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        intent?.getParcelableExtra<LogConfig>(LOG_CONFIG)?.let {
            if (startLogConfig == null) {
                startLogConfig = it
            }
            binder.logConfig = it
        }
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    private fun showForegroundNotification(@StringRes titleResId: Int, @StringRes textResId: Int) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, LogCollectorActivity::class.java),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            } else PendingIntent.FLAG_UPDATE_CURRENT
        )
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(titleResId))
            .setContentText(getString(textResId))
            .setContentIntent(pendingIntent)
        val notification =
            NotificationUtils.buildNotification(this, builder, "LogcatService")
        startForeground(titleResId, notification)
    }
}

class LogcatServiceImpl : Binder() {
    companion object {
        private const val TAG = "LogcatServiceImpl"
    }

    private var mLogCollector: LogCollector? = null

    val logLists = mutableListOf<MutableList<LogWrapper>>()

    private val mLogChangedListeners = mutableListOf<LogCollector.LogChangedListener>()

    private val mLogUpdatedListeners = CopyOnWriteArraySet<LogUpdatedListener>()

    internal var logConfig: LogConfig? = null
        set(value) {
            if (field == value) return
            field = value
            startLogCollector()
        }

    private fun startLogCollector() {
        reset()
        val localLogConfig = logConfig ?: return
        localLogConfig.logcatTypes.let { list ->
            for (idx in list.indices) {
                logLists.add(Collections.synchronizedList(RingList(list[idx].cacheLines)))
                object : LogCollector.LogChangedListener {
                    private val mFilterPattern: Pattern? by lazy {
                        list[idx].regex.let {
                            if (it.isNullOrBlank()) return@lazy null
                            else return@lazy try {
                                Pattern.compile("(?=.*($it)).*")
                            } catch (e: PatternSyntaxException) {
                                Log.e(TAG, "logcatTypes[$idx] = ${list[idx]} is invalid", e)
                                null
                            }
                        }
                    }

                    override fun filter(logWrapper: LogWrapper): Boolean {
                        return mFilterPattern?.matcher(logWrapper)?.matches() ?: true
                    }

                    override fun onLogChanged(addedLogList: List<LogWrapper>) {
                        logLists[idx].addAll(addedLogList)
                        mLogUpdatedListeners.forEach {
                            it.onLogUpdated(idx)
                        }
                    }
                }.let { mLogChangedListeners.add(it) }
            }
        }
        mLogCollector = LogCollector(localLogConfig).apply {
            mLogChangedListeners.forEach {
                addLogChangedListener(it)
            }
            start(object : LogCollector.OnStopCallback {
                override fun onStop(isInterrupted: Boolean): Boolean = !isInterrupted
            })
        }
    }

    private fun reset() {
        logLists.clear()
        mLogCollector?.run {
            stop()
            mLogChangedListeners.forEach {
                removeLogChangedListener(it)
            }
        }
        mLogChangedListeners.clear()
        mLogCollector = null
    }

    fun clearLog(logTypeIndex: Int) {
        logLists[logTypeIndex].clear()
    }

    interface LogUpdatedListener {
        fun onLogUpdated(logTypeIndex: Int)
    }

    fun addLogUpdateListener(listener: LogUpdatedListener) {
        mLogUpdatedListeners.add(listener)
    }

    fun removeLogUpdateListener(listener: LogUpdatedListener) {
        mLogUpdatedListeners.remove(listener)
    }
}