package com.hym.zhankukotlin

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.google.gson.GsonBuilder
import com.hym.zhankukotlin.model.ZkTypeAdapterFactory
import com.hym.zhankukotlin.network.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import me.weishu.reflection.Reflection
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class MyApplication : Application(), ViewModelStoreOwner, HasDefaultViewModelProviderFactory {
    private val viewModelStore = ViewModelStore()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Reflection.unseal(base)
    }

    override fun onCreate() {
        super.onCreate()

        //setTheme(R.style.Theme_ZhanKuKotlin)
        theme.applyStyle(R.style.Theme_ZhanKuKotlin, true)
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

        CookieManager.newInstance(this)
        val clientCacheDir = File(cacheDir, CLIENT_CACHE_DIR_NAME)
        if (!clientCacheDir.isDirectory) {
            clientCacheDir.mkdirs()
        }
        okHttpClient = OkHttpClient.Builder()
            .cookieJar(CookieManager.INSTANCE)
            .addInterceptor(HeaderInterceptor())
            .addNetworkInterceptor(LogInterceptor())
            .cache(Cache(clientCacheDir, 100 * 1024 * 1024))
            //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
            .build()
        retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.API_URL)
            .addConverterFactory(
                GsonConverterFactory.create(
                    GsonBuilder().registerTypeAdapterFactory(ZkTypeAdapterFactory).create()
                )
            )
            .callbackExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
            .build()
        networkService = retrofit.create(NetworkService::class.java)
        transparentDrawable = ContextCompat.getDrawable(this, R.drawable.transparent)!!

        SubsamplingScaleImageView.setPreferredBitmapConfig(Bitmap.Config.ARGB_8888)

        val deferredList: MutableList<Deferred<*>> = mutableListOf()
        deferredList.add(getAppViewModel<MyAppViewModel>().getCategoryItemsFromNetworkAsync())
        runBlocking {
            deferredList.awaitAll()
        }
    }

    override fun getViewModelStore(): ViewModelStore = viewModelStore

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory =
        ViewModelProvider.AndroidViewModelFactory.getInstance(this)

    companion object {
        private const val TAG = "MyApplication"
        private const val CLIENT_CACHE_DIR_NAME = "retrofit"

        lateinit var INSTANCE: MyApplication

        @JvmStatic
        lateinit var okHttpClient: OkHttpClient

        @JvmStatic
        lateinit var retrofit: Retrofit

        @JvmStatic
        lateinit var networkService: NetworkService
            private set

        @JvmStatic
        lateinit var transparentDrawable: Drawable
            private set
    }
}

inline fun <reified VM : ViewModel> getAppViewModel() =
    ViewModelProvider(MyApplication.INSTANCE)[VM::class.java]