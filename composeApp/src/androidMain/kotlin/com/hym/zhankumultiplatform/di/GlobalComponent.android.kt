package com.hym.zhankumultiplatform.di

import android.app.Application
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.hym.zhankumultiplatform.MyApplication
import com.hym.zhankumultiplatform.network.CookieManager
import com.hym.zhankumultiplatform.network.HeaderInterceptor
import com.hym.zhankumultiplatform.network.LogInterceptor
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

/**
 * @author hehua2008
 * @date 2022/8/4
 */
@Component
@ApplicationScope
abstract class AndroidComponent {
    companion object {
        private const val OKHTTP_CACHE_DIR_NAME = "okhttp"
        private const val EXO_CACHE_DIR_NAME = "exocache"

        val instance: AndroidComponent = AndroidComponent::class.create()
    }

    abstract val okHttpClient: OkHttpClient

    abstract val exoDataSourceFactory: DataSource.Factory

    abstract val exoDatabaseProvider: DatabaseProvider

    abstract val exoCacheDataSourceFactory: CacheDataSource.Factory

    @ApplicationScope
    @Provides
    fun provideApplication(): Application {
        return MyApplication.INSTANCE
    }

    @ApplicationScope
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

    @ApplicationScope
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
    @ApplicationScope
    @Provides
    fun provideExoDatabaseProvider(app: Application): DatabaseProvider {
        return StandaloneDatabaseProvider(app)
    }

    @ApplicationScope
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
            LeastRecentlyUsedCacheEvictor(250 * 1024 * 1024),
            exoDatabaseProvider
        )

        // Configure the DataSource.Factory with the cache and factory for the desired HTTP stack.
        return CacheDataSource.Factory()
            .setCache(exoCache)
            .setUpstreamDataSourceFactory(upstreamDataSourceFactory)
    }
}
