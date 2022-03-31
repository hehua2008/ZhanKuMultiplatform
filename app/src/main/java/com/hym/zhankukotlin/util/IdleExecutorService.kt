package com.hym.zhankukotlin.util

import android.os.Build
import android.os.Looper
import android.os.MessageQueue
import androidx.annotation.RequiresApi
import java.util.concurrent.*
import kotlin.concurrent.thread

/**
 * @author hehua2008
 * @date 2022/3/31
 */
class IdleExecutorService(
    private val delegate: ExecutorService,
    private val delayMills: Long = 200
) : ExecutorService by delegate {
    @RequiresApi(Build.VERSION_CODES.M)
    private val pendingWrappers = DelayQueue<IdleHandlerWrapper>()

    @RequiresApi(Build.VERSION_CODES.M)
    private val checkIdleTimeoutThread = thread(name = "check-idle-timeout-thread") {
        try {
            while (!Thread.interrupted()) {
                val wrapper = pendingWrappers.take()
                if (checkIsShutdown()) break
                delegate.execute(wrapper.command)
            }
        } catch (e: InterruptedException) {
            // ignore
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkIsShutdown(): Boolean {
        if (!delegate.isShutdown) return false
        pendingWrappers.clear()
        checkIdleTimeoutThread.interrupt()
        return true
    }

    override fun execute(command: Runnable?) {
        if (command == null || Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            delegate.execute(command)
        } else {
            IdleHandlerWrapper(command, System.currentTimeMillis() + delayMills).let {
                pendingWrappers.offer(it)
                Looper.getMainLooper().queue.addIdleHandler(it)
            }
        }
    }

    override fun shutdown() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkIdleTimeoutThread.interrupt()
        }
        delegate.shutdown()
    }

    override fun shutdownNow(): MutableList<Runnable> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkIdleTimeoutThread.interrupt()
        }
        return delegate.shutdownNow()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private inner class IdleHandlerWrapper(val command: Runnable, val delayTime: Long) :
        MessageQueue.IdleHandler, Delayed {

        override fun queueIdle(): Boolean {
            if (checkIsShutdown()) return false
            if (!pendingWrappers.remove(this)) return false
            try {
                delegate.execute(command)
            } catch (e: RejectedExecutionException) {
                // ignore
            }
            return false
        }

        override fun compareTo(other: Delayed?): Int {
            if (this === other) return 0
            other as IdleHandlerWrapper
            return delayTime.compareTo(other.delayTime)
        }

        override fun getDelay(unit: TimeUnit): Long {
            return unit.convert(delayTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS)
        }
    }
}