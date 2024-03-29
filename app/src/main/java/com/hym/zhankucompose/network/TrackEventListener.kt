package com.hym.zhankucompose.network

import android.util.Log
import okhttp3.Call
import okhttp3.Connection
import okhttp3.EventListener
import okhttp3.Handshake
import okhttp3.HttpUrl
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.atomic.AtomicLong

/**
 * @author hehua2008
 * @date 2024/3/29
 */
class TrackEventListener private constructor(
    private val logger: HttpLoggingInterceptor.Logger
) : EventListener() {
    companion object {
        private const val TAG = "TrackEventListener"

        private val CALL_ID = AtomicLong()
    }

    private val callId = CALL_ID.incrementAndGet()

    override fun callStart(call: Call) {
        logger.log("[$callId] callStart: ${call.request()}")
    }

    override fun proxySelectStart(call: Call, url: HttpUrl) {
        logger.log("[$callId] proxySelectStart: $url")
    }

    override fun proxySelectEnd(call: Call, url: HttpUrl, proxies: List<Proxy>) {
        logger.log("[$callId] proxySelectEnd: $proxies")
    }

    override fun dnsStart(call: Call, domainName: String) {
        logger.log("[$callId] dnsStart: $domainName")
    }

    override fun dnsEnd(call: Call, domainName: String, inetAddressList: List<InetAddress>) {
        logger.log("[$callId] dnsEnd: $inetAddressList")
    }

    override fun connectStart(call: Call, inetSocketAddress: InetSocketAddress, proxy: Proxy) {
        logger.log("[$callId] connectStart: $inetSocketAddress $proxy")
    }

    override fun secureConnectStart(call: Call) {
        logger.log("[$callId] secureConnectStart")
    }

    override fun secureConnectEnd(call: Call, handshake: Handshake?) {
        logger.log("[$callId] secureConnectEnd: $handshake")
    }

    override fun connectEnd(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?
    ) {
        logger.log("[$callId] connectEnd: $protocol")
    }

    override fun connectFailed(
        call: Call,
        inetSocketAddress: InetSocketAddress,
        proxy: Proxy,
        protocol: Protocol?,
        ioe: IOException
    ) {
        logger.log("[$callId] connectFailed: $protocol $ioe")
    }

    override fun connectionAcquired(call: Call, connection: Connection) {
        logger.log("[$callId] connectionAcquired: $connection")
    }

    override fun connectionReleased(call: Call, connection: Connection) {
        logger.log("[$callId] connectionReleased")
    }

    override fun requestHeadersStart(call: Call) {
        logger.log("[$callId] requestHeadersStart")
    }

    override fun requestHeadersEnd(call: Call, request: Request) {
        logger.log("[$callId] requestHeadersEnd")
    }

    override fun requestBodyStart(call: Call) {
        logger.log("[$callId] requestBodyStart")
    }

    override fun requestBodyEnd(call: Call, byteCount: Long) {
        logger.log("[$callId] requestBodyEnd: byteCount=$byteCount")
    }

    override fun requestFailed(call: Call, ioe: IOException) {
        logger.log("[$callId] requestFailed: $ioe")
    }

    override fun responseHeadersStart(call: Call) {
        logger.log("[$callId] responseHeadersStart")
    }

    override fun responseHeadersEnd(call: Call, response: Response) {
        logger.log("[$callId] responseHeadersEnd: $response")
    }

    override fun responseBodyStart(call: Call) {
        logger.log("[$callId] responseBodyStart")
    }

    override fun responseBodyEnd(call: Call, byteCount: Long) {
        logger.log("[$callId] responseBodyEnd: byteCount=$byteCount")
    }

    override fun responseFailed(call: Call, ioe: IOException) {
        logger.log("[$callId] responseFailed: $ioe")
    }

    override fun callEnd(call: Call) {
        logger.log("[$callId] callEnd")
    }

    override fun callFailed(call: Call, ioe: IOException) {
        logger.log("[$callId] callFailed: $ioe")
    }

    override fun canceled(call: Call) {
        logger.log("[$callId] canceled")
    }

    override fun satisfactionFailure(call: Call, response: Response) {
        logger.log("[$callId] satisfactionFailure: $response")
    }

    override fun cacheHit(call: Call, response: Response) {
        logger.log("[$callId] cacheHit: $response")
    }

    override fun cacheMiss(call: Call) {
        logger.log("[$callId] cacheMiss")
    }

    override fun cacheConditionalHit(call: Call, cachedResponse: Response) {
        logger.log("[$callId] cacheConditionalHit: $cachedResponse")
    }

    open class Factory @JvmOverloads constructor(
        private val logger: HttpLoggingInterceptor.Logger = HttpLoggingInterceptor.Logger { message ->
            Log.i(TAG, message)
        }
    ) : EventListener.Factory {
        override fun create(call: Call): EventListener = TrackEventListener(logger)
    }
}
