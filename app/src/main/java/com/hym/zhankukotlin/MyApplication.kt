package com.hym.zhankukotlin

import android.app.Application
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import com.hym.zhankukotlin.network.*
import me.weishu.reflection.Reflection
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File

class MyApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Reflection.unseal(base)
    }

    override fun onCreate() {
        super.onCreate()

        setTheme(R.style.Theme_ZhanKuKotlin)
        INSTANCE = this

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
        sClient = OkHttpClient.Builder()
                .cookieJar(CookieManager.INSTANCE)
                .addInterceptor(HeaderInterceptor())
                .addNetworkInterceptor(LogInterceptor())
                .cache(Cache(clientCacheDir, 100 * 1024 * 1024))
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
                .build()
        sRetrofit = Retrofit.Builder()
                .client(sClient)
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(ItemConverterFactory.INSTANCE)
                .callbackExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                .build()
        networkService = sRetrofit.create(NetworkService::class.java)
        transparentDrawable = getDrawable(R.drawable.transparent)!!
    }

    companion object {
        private val TAG = MyApplication::class.java.simpleName
        private const val CLIENT_CACHE_DIR_NAME = "retrofit"

        lateinit var INSTANCE: MyApplication

        @JvmStatic
        private lateinit var sClient: OkHttpClient

        @JvmStatic
        private lateinit var sRetrofit: Retrofit

        @JvmStatic
        lateinit var networkService: NetworkService
            private set

        @JvmStatic
        lateinit var transparentDrawable: Drawable
            private set
    }
}