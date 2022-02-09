package com.hym.zhankukotlin.player

import android.content.Context
import android.util.AttributeSet
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

class CustomPlayerView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : PlayerView(context, attrs, defStyleAttr) {
    override fun setPlayer(newPlayer: Player?) {
        if (newPlayer == null) {
            player?.playerView = null
            super.setPlayer(null)
        } else {
            newPlayer.playerView = this
        }
    }

    fun superSetPlayer(player: Player?) {
        super.setPlayer(player)
    }
}