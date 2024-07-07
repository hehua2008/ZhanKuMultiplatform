package com.hym.zhankumultiplatform

import android.app.Application
import android.net.ConnectivityManager
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner

class MyApplication : Application(), ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    override fun onCreate() {
        super.onCreate()

        // Eagerly initialize ConnectivityManager with the application context to avoid memory leak.
        getSystemService(ConnectivityManager::class.java)

        INSTANCE = this

        registerActivityLifecycleCallbacks(MyActivityLifecycleCallbacks)

        /*
        mainLooper.setMessageLogging(object : Printer {
            private val TIME_OUT = 30
            private var mIsStart = true
            private var mStartTime: Long = 0L
            private var mStartLog: String? = null

            override fun println(x: String?) {
                if (mIsStart) {
                    mIsStart = false
                    mStartTime = SystemClock.elapsedRealtime()
                    mStartLog = x
                } else {
                    mIsStart = true
                    val costTime = SystemClock.elapsedRealtime() - mStartTime
                    if (costTime > TIME_OUT) {
                        Logger.w(TAG, "cost time: $costTime\n$mStartLog\n$x")
                    }
                }
            }
        })
        */

        getAppViewModel<MyAppViewModel>().getCategoryItemsFromNetworkAsync()
    }

    override val viewModelStore: ViewModelStore = ViewModelStore()

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = ViewModelProvider.AndroidViewModelFactory.getInstance(this)

    companion object {
        private const val TAG = "MyApplication"

        lateinit var INSTANCE: MyApplication
            private set
    }
}
