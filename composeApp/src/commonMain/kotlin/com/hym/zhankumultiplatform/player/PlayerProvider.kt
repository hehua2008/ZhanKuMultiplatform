package com.hym.zhankumultiplatform.player

/**
 * @author hehua2008
 * @date 2024/7/6
 */
expect class PlayerProvider(maxPoolSize: Int = 3) {
    fun obtain(): Player

    fun recycle(player: Player): Boolean

    fun pauseOtherActivePlayers(player: Player?)

    fun onCleared()
}
