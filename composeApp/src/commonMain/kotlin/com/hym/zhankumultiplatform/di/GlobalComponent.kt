package com.hym.zhankumultiplatform.di

import com.hym.zhankumultiplatform.network.FileStorage
import com.hym.zhankumultiplatform.network.NetworkConstants
import com.hym.zhankumultiplatform.network.NetworkService
import com.hym.zhankumultiplatform.util.Logger
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
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides
import okio.FileSystem

/**
 * @author hehua2008
 * @date 2022/8/4
 */
@Component
@ApplicationScope
abstract class GlobalComponent {
    companion object {
        val Instance: GlobalComponent = createGlobalComponent()
    }

    abstract val httpClient: HttpClient

    abstract val networkService: NetworkService

    @ApplicationScope
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
                logger = object : io.ktor.client.plugins.logging.Logger {
                    override fun log(message: String) {
                        Logger.d("HttpClient", message)
                    }
                }
                level = LogLevel.HEADERS
                //sanitizeHeader { header -> header == HttpHeaders.Authorization }
            }
        }
    }

    @ApplicationScope
    @Provides
    fun provideNetworkService(httpClient: HttpClient): NetworkService {
        return NetworkService(httpClient)
    }
}

@KmpComponentCreate
expect fun GlobalComponent.Companion.createGlobalComponent(): GlobalComponent
