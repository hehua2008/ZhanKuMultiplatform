package com.hym.zhankucompose.player

import android.util.Log
import androidx.core.util.Pools
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player

class PlayerProvider(maxPoolSize: Int = 3) {
    private val mTag = "PlayerProvider@${Integer.toHexString(hashCode())}"

    private val mPlayerPool = PlayerPool(maxPoolSize)
    private val mActivePlayers: MutableSet<Player> = mutableSetOf()

    @Synchronized
    fun obtain(): Player {
        return mPlayerPool.acquire()
            .also {
                mActivePlayers.add(it)
                Log.d(mTag, "The number of active players increased to ${mActivePlayers.size}")
            }
    }

    @Synchronized
    fun recycle(player: Player): Boolean {
        player.apply {
            pause()
            clearMediaItems()
        }
        mActivePlayers.remove(player)
        Log.d(mTag, "The number of active players is reduced to ${mActivePlayers.size}")
        return mPlayerPool.release(player)
    }

    @Synchronized
    private fun forEachActivePlayer(action: (player: Player) -> Unit) {
        mActivePlayers.forEach(action)
    }

    @Synchronized
    fun pauseOtherActivePlayers(player: Player?) {
        forEachActivePlayer {
            if (it !== player) {
                it.pause()
            }
        }
    }

    @Synchronized
    fun onCleared() {
        forEachActivePlayer { PlayerFactory.destroy(it) }
        mActivePlayers.clear()
        mPlayerPool.onCleared()
    }

    private inner class PlayerWrapper(player: Player) : ForwardingPlayer(player), Player.Listener {
        init {
            // I have no idea why some times we can't receive Player.STATE_READY state !!!
            // addListener(this)
        }

        override fun play() {
            //if (playbackState == Player.STATE_READY) {
            pauseOtherActivePlayers(this)
            //}
            super.play()
        }

        override fun setPlayWhenReady(playWhenReady: Boolean) {
            if (playWhenReady/* && playbackState == Player.STATE_READY*/) {
                pauseOtherActivePlayers(this)
            }
            super.setPlayWhenReady(playWhenReady)
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            if (playbackState == Player.STATE_READY) {
                pauseOtherActivePlayers(this)
            }
        }
    }

    private inner class PlayerPool(maxPoolSize: Int = 3) : Pools.SimplePool<Player>(maxPoolSize) {
        override fun acquire(): Player {
            return super.acquire() ?: PlayerWrapper(PlayerFactory.create())
        }

        override fun release(player: Player): Boolean {
            val recycled = super.release(player)
            if (!recycled) {
                PlayerFactory.destroy(player)
            }
            return recycled
        }

        fun onCleared() {
            while (true) {
                val player = super.acquire() ?: break
                PlayerFactory.destroy(player)
            }
        }
    }
}