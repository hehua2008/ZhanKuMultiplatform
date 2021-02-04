package com.hym.zhankukotlin

import android.app.Application
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.util.Log
import com.hym.zhankukotlin.network.*
import com.nostra13.universalimageloader.cache.disc.DiskCache
import com.nostra13.universalimageloader.cache.disc.impl.ext.LruDiskCache
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration
import com.nostra13.universalimageloader.core.assist.QueueProcessingType
import me.weishu.reflection.Reflection
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import java.io.IOException

class MyApplication : Application() {
    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        Reflection.unseal(base)
    }

    override fun onCreate() {
        super.onCreate()

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
        val imagesCacheDir = File(cacheDir, IMAGES_CACHE_DIR_NAME)
        if (!imagesCacheDir.isDirectory) {
            imagesCacheDir.mkdirs()
        }
        var diskCache: DiskCache? = null
        try {
            diskCache = LruDiskCache(imagesCacheDir, Md5FileNameGenerator(), 500 * 1024 * 1024)
        } catch (e: IOException) {
            Log.w(TAG, "init disk cache failed", e)
        }
        val options = DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build()
        val config = ImageLoaderConfiguration.Builder(this)
            .tasksProcessingOrder(QueueProcessingType.LIFO)
            .memoryCacheExtraOptions(1000, 10000)
            .memoryCacheSize((Runtime.getRuntime().maxMemory() / 5).toInt())
            .denyCacheImageMultipleSizesInMemory()
            .diskCache(diskCache)
            .defaultDisplayImageOptions(options)
            .writeDebugLogs()
            .build()
        imageLoader = ImageLoader.getInstance()
        imageLoader.init(config)
        transparentDrawable = getDrawable(R.drawable.transparent)
    }

    companion object {
        private val TAG = MyApplication::class.java.simpleName
        private const val CLIENT_CACHE_DIR_NAME = "retrofit"
        private const val IMAGES_CACHE_DIR_NAME = "images"

        @JvmStatic
        private lateinit var sClient: OkHttpClient

        @JvmStatic
        private lateinit var sRetrofit: Retrofit

        @JvmStatic
        lateinit var networkService: NetworkService
            private set

        @JvmStatic
        lateinit var imageLoader: ImageLoader
            private set

        @JvmStatic
        var transparentDrawable: Drawable? = null
            private set
    }
}