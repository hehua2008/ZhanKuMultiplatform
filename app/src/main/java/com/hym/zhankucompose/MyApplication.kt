package com.hym.zhankucompose

import android.app.Application
import android.graphics.Bitmap
import android.net.ConnectivityManager
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking

@HiltAndroidApp
class MyApplication : Application(), ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    override fun onCreate() {
        super.onCreate()

        // Eagerly initialize ConnectivityManager with the application context to avoid memory leak.
        getSystemService(ConnectivityManager::class.java)

        //setTheme(R.style.Theme_ZhanKuCompose)
        theme.applyStyle(R.style.Theme_ZhanKuCompose, true)
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
                        Log.w(TAG, "cost time: $costTime\n$mStartLog\n$x")
                    }
                }
            }
        })
        */

        SubsamplingScaleImageView.setPreferredBitmapConfig(Bitmap.Config.ARGB_8888)

        val deferredList: MutableList<Deferred<*>> = mutableListOf()
        deferredList.add(getAppViewModel<MyAppViewModel>().getCategoryItemsFromNetworkAsync())
        runBlocking {
            deferredList.awaitAll()
        }
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

inline fun <reified VM : ViewModel> getAppViewModel() =
    ViewModelProvider(MyApplication.INSTANCE)[VM::class.java]