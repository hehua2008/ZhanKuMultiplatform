package com.hym.zhankukotlin.ui.photoview

/**
 * @author hehua2008
 * @date 2022/3/7
 */
interface PhotoViewCallback {
    fun addScreenListener(position: Int, listener: OnScreenListener)

    fun removeScreenListener(position: Int)

    fun toggleFullScreen()

    fun getCurrentPosition(): Int
}