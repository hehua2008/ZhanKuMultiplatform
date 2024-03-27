package com.hym.zhankucompose.hilt

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.hym.zhankucompose.R
import com.hym.zhankucompose.model.Cate
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.network.Constants
import com.hym.zhankucompose.network.ConverterFactoryDelegate
import com.hym.zhankucompose.network.CookieManager
import com.hym.zhankucompose.network.HeaderInterceptor
import com.hym.zhankucompose.network.LogInterceptor
import com.hym.zhankucompose.network.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import okhttp3.Cache
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.io.File
import javax.inject.Singleton

/**
 * @author hehua2008
 * @date 2022/8/4
 */
private val JsonDefault = Json {
    ignoreUnknownKeys = true
    SerializersModule {
        polymorphic(Cate::class) {
            subclass(TopCate::class, TopCate.TopCateTypeAdapter)
            subclass(SubCate::class, SubCate.SubCateTypeAdapter)
        }
    }
}

private val JsonDefaultFactory = JsonDefault.asConverterFactory("application/json".toMediaType())

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {
    companion object {
        private const val OKHTTP_CACHE_DIR_NAME = "okhttp"
    }

    @Singleton
    @Provides
    fun provideOkHttp(app: Application, cookieManager: CookieManager): OkHttpClient {
        val okHttpCacheDir = File(app.cacheDir, OKHTTP_CACHE_DIR_NAME)
        if (!okHttpCacheDir.isDirectory) {
            okHttpCacheDir.deleteRecursively()
            okHttpCacheDir.mkdirs()
        }
        return OkHttpClient.Builder()
            .cookieJar(cookieManager)
            .addInterceptor(HeaderInterceptor())
            .addNetworkInterceptor(LogInterceptor())
            .cache(Cache(okHttpCacheDir, 200 * 1024 * 1024))
            //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.API_URL)
            .addConverterFactory(ConverterFactoryDelegate(JsonDefaultFactory))
            .callbackExecutor(Dispatchers.IO.asExecutor())
            .build()
    }

    @Singleton
    @Provides
    fun provideNetworkService(retrofit: Retrofit): NetworkService {
        return retrofit.create(NetworkService::class.java)
    }

    @Singleton
    @Provides
    fun provideTransparentDrawable(app: Application): Drawable {
        return ContextCompat.getDrawable(app, R.drawable.transparent)!!
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Accessor {
        fun okHttpClient(): OkHttpClient

        fun retrofit(): Retrofit

        fun networkService(): NetworkService

        fun transparentDrawable(): Drawable
    }
}