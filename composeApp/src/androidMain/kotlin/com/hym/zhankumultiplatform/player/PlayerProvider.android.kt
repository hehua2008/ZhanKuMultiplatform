package com.hym.zhankumultiplatform.player

import androidx.core.util.Pools
import androidx.media3.common.Player
import com.hym.zhankumultiplatform.util.Logger

actual class PlayerProvider actual constructor(maxPoolSize: Int) {
    private val mTag = "PlayerProvider@${Integer.toHexString(hashCode())}"

    private val mPlayerPool = PlayerPool(maxPoolSize)
    private val mActivePlayers: MutableSet<Player> = mutableSetOf()

    @Synchronized
    actual fun obtain(): Player {
        return mPlayerPool.acquire()
            .also {
                mActivePlayers.add(it)
                Logger.d(mTag, "The number of active players increased to ${mActivePlayers.size}")
            }
    }

    @Synchronized
    actual fun recycle(player: Player): Boolean {
        player.apply {
            pause()
            clearMediaItems()
        }
        mActivePlayers.remove(player)
        Logger.d(mTag, "The number of active players is reduced to ${mActivePlayers.size}")
        return mPlayerPool.release(player)
    }

    @Synchronized
    actual fun pauseOtherActivePlayers(player: Player?) {
        mActivePlayers.forEach {
            if (it !== player) {
                it.pause()
            }
        }
    }

    @Synchronized
    actual fun onCleared() {
        mActivePlayers.forEach {
            PlayerFactory.destroy(it)
        }
        mActivePlayers.clear()
        mPlayerPool.onCleared()
    }

    private inner class PlayerWrapper(val delegate: Player) : Player by delegate {
        override fun play() {
            pauseOtherActivePlayers(this)
            delegate.play()
        }

        override fun setPlayWhenReady(playWhenReady: Boolean) {
            if (playWhenReady) {
                pauseOtherActivePlayers(this)
            }
            delegate.playWhenReady = playWhenReady
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