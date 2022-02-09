package com.hym.zhankukotlin.player

import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView
import java.lang.ref.WeakReference
import java.util.*

private val mPlayerViewMap: MutableMap<Player, WeakReference<PlayerView>?> = WeakHashMap()

var Player.playerView: PlayerView?
    set(value) {
        synchronized(mPlayerViewMap) {
            switchPlayerView(mPlayerViewMap[this]?.get(), value)
            mPlayerViewMap[this] = if (value == null) null else WeakReference(value)
        }
    }
    get() = mPlayerViewMap[this]?.get()

private fun Player.switchPlayerView(oldPlayerView: PlayerView?, newPlayerView: PlayerView?) {
    if (oldPlayerView === newPlayerView) return
    if (newPlayerView is CustomPlayerView) {
        newPlayerView.superSetPlayer(this)
    } else {
        newPlayerView?.player = this
    }
    if (oldPlayerView is CustomPlayerView) {
        oldPlayerView.superSetPlayer(null)
    } else {
        oldPlayerView?.player = null
    }
}