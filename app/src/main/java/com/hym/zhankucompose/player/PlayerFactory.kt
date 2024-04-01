package com.hym.zhankucompose.player

import android.app.Application
import android.util.Log
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import com.hym.zhankucompose.MyApplication
import com.hym.zhankucompose.hilt.NetworkModule
import dagger.hilt.android.EntryPointAccessors
import java.util.concurrent.atomic.AtomicInteger

object PlayerFactory {
    private const val TAG = "PlayerFactory"

    private val APP_CONTEXT: Application by lazy { MyApplication.INSTANCE }
    private val COUNT = AtomicInteger(0)
    private val cacheDataSourceFactory =
        EntryPointAccessors.fromApplication(APP_CONTEXT, NetworkModule.Accessor::class.java)
            .exoCacheDataSourceFactory()

    fun create(): Player {
        val count = COUNT.incrementAndGet()
        Log.d(TAG, "create a new player instance (current count=$count)")
        return ExoPlayer.Builder(APP_CONTEXT)
            .setMediaSourceFactory(
                DefaultMediaSourceFactory(APP_CONTEXT).setDataSourceFactory(cacheDataSourceFactory)
            )
            .build()
    }

    fun destroy(player: Player) {
        val count = COUNT.decrementAndGet()
        Log.d(TAG, "destroy a player instance (current count=$count)")
        player.release()
    }
}