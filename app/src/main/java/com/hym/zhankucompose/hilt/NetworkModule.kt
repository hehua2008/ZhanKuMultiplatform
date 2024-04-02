package com.hym.zhankucompose.hilt

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.bumptech.glide.load.engine.cache.DiskCache
import com.hym.zhankucompose.R
import com.hym.zhankucompose.model.Cate
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.network.Constants
import com.hym.zhankucompose.network.ConverterFactoryDelegate
import com.hym.zhankucompose.network.CookieManager
import com.hym.zhankucompose.network.HeaderInterceptor
import com.hym.zhankucompose.network.ImageNetInterceptor
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
import javax.inject.Named
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
object NetworkModule {
    private const val OKHTTP_CACHE_DIR_NAME = "okhttp"
    private const val EXO_CACHE_DIR_NAME = "exocache"

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
            //.eventListenerFactory(TrackEventListener.Factory())
            .build()
    }

    @Named("ImageOkHttp")
    @Singleton
    @Provides
    fun provideImageOkHttp(okHttpClient: OkHttpClient): OkHttpClient {
        return okHttpClient.newBuilder()
            //.addInterceptor(ImageInterceptor())
            .addNetworkInterceptor(ImageNetInterceptor())
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

    @Singleton
    @Provides
    fun provideExoDataSourceFactory(
        app: Application,
        okHttpClient: OkHttpClient
    ): DataSource.Factory {
        // Given a OkHttpClient, build a OkHttpDataSource.Factory.
        val okHttpDataSourceFactory = OkHttpDataSource.Factory(okHttpClient)
        // Wrap the OkHttpDataSource.Factory in a DefaultDataSource.Factory, which adds in support
        // for requesting data from other sources (such as files, resources, etc).
        return DefaultDataSource.Factory(app, okHttpDataSourceFactory)
    }

    // Note: This should be a singleton in your app.
    @Singleton
    @Provides
    fun provideExoDatabaseProvider(app: Application): DatabaseProvider {
        return StandaloneDatabaseProvider(app)
    }

    @Singleton
    @Provides
    fun provideExoCacheDataSourceFactory(
        app: Application,
        exoDatabaseProvider: DatabaseProvider,
        upstreamDataSourceFactory: DataSource.Factory
    ): CacheDataSource.Factory {
        val exoCacheDir = File(app.cacheDir, EXO_CACHE_DIR_NAME)
        if (!exoCacheDir.isDirectory) {
            exoCacheDir.deleteRecursively()
            exoCacheDir.mkdirs()
        }
        // An on-the-fly cache should evict media when reaching a maximum disk space limit.
        val exoCache = SimpleCache(
            exoCacheDir,
            // Keep the same cache size as image cache
            LeastRecentlyUsedCacheEvictor(DiskCache.Factory.DEFAULT_DISK_CACHE_SIZE.toLong()),
            exoDatabaseProvider
        )

        // Configure the DataSource.Factory with the cache and factory for the desired HTTP stack.
        return CacheDataSource.Factory()
            .setCache(exoCache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface Accessor {
        fun okHttpClient(): OkHttpClient

        @Named("ImageOkHttp")
        fun imageOkHttpClient(): OkHttpClient

        fun retrofit(): Retrofit

        fun networkService(): NetworkService

        fun transparentDrawable(): Drawable

        fun exoCacheDataSourceFactory(): CacheDataSource.Factory
    }
}