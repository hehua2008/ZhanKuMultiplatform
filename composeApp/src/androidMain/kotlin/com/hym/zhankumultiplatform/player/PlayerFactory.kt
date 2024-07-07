package com.hym.zhankumultiplatform.player

import android.app.Application
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.hym.zhankumultiplatform.MyApplication
import com.hym.zhankumultiplatform.di.AndroidComponent
import com.hym.zhankumultiplatform.util.Logger
import java.util.concurrent.atomic.AtomicInteger

object PlayerFactory {
    private const val TAG = "PlayerFactory"

    private val APP_CONTEXT: Application by lazy { MyApplication.INSTANCE }
    private val COUNT = AtomicInteger(0)
    private val cacheDataSourceFactory = AndroidComponent.instance.exoCacheDataSourceFactory

    fun create(): Player {
        val count = COUNT.incrementAndGet()
        Logger.d(TAG, "create a new player instance (current count=$count)")
        return ExoPlayer.Builder(APP_CONTEXT)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(APP_CONTEXT).setDataSourceFactory(cacheDataSourceFactory)
            )
            .build()
    }

    fun destroy(player: Player) {
        val count = COUNT.decrementAndGet()
        Logger.d(TAG, "destroy a player instance (current count=$count)")
        player.release()
    }
}