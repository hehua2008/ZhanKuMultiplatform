package com.hym.zhankumultiplatform.player

/**
 * @author hehua2008
 * @date 2024/7/6
 */
actual class PlayerProvider actual constructor(maxPoolSize: Int) {
    actual fun obtain(): Player {
        TODO("Not yet implemented")
    }

    actual fun recycle(player: Player): Boolean {
        // TODO
        return true
    }

    actual fun pauseOtherActivePlayers(player: Player?) {
        // TODO
    }

    actual fun onCleared() {
        // TODO
    }
}
