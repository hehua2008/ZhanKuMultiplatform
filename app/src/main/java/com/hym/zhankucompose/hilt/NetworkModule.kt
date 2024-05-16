package com.hym.zhankucompose.hilt

import android.app.Application
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.media3.database.DatabaseProvider
import androidx.media3.database.StandaloneDatabaseProvider
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.datasource.okhttp.OkHttpDataSource
import com.hym.zhankucompose.R
import com.hym.zhankucompose.network.CookieManager
import com.hym.zhankucompose.network.FileStorage
import com.hym.zhankucompose.network.HeaderInterceptor
import com.hym.zhankucompose.network.ImageNetInterceptor
import com.hym.zhankucompose.network.LogInterceptor
import com.hym.zhankucompose.network.NetworkConstants
import com.hym.zhankucompose.network.NetworkService
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.UserAgent
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.ConstantCookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import okhttp3.Cache
import okhttp3.OkHttpClient
import okio.FileSystem
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

/**
 * @author hehua2008
 * @date 2022/8/4
 */
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
    fun provideHttpClient(): HttpClient {
        return HttpClient {
            //expectSuccess = true
            followRedirects = true

            install(DefaultRequest) {
                url {
                    protocol = URLProtocol.HTTPS
                }
            }

            install(UserAgent) {
                agent = NetworkConstants.USER_AGENT
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }

            install(ContentNegotiation) {
                json(NetworkConstants.JsonDefault)
            }

            install(HttpCookies) {
                storage = ConstantCookiesStorage()
            }

            /*
            install(ContentEncoding) {
                deflate(1.0f)
                gzip(0.9f)
            }
            */

            install(HttpCache) {
                val cacheFile = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "KtorHttpCache"
                publicStorage(FileStorage(cacheFile))
            }

            install(HttpTimeout) {
                requestTimeoutMillis = 60_000
            }

            install(Logging) {
                logger = object : Logger {
                    override fun log(message: String) {
                        Log.d("HttpClient", message)
                    }
                }
                level = LogLevel.HEADERS
                //sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
        }
    }

    @Singleton
    @Provides
    fun provideNetworkService(httpClient: HttpClient): NetworkService {
        return NetworkService(httpClient)
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
            LeastRecentlyUsedCacheEvictor(250 * 1024 * 1024),
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

        fun networkService(): NetworkService

        fun transparentDrawable(): Drawable

        fun exoCacheDataSourceFactory(): CacheDataSource.Factory
    }
}
