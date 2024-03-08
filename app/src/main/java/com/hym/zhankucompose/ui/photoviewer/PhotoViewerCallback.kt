package com.hym.zhankucompose.ui.photoviewer

/**
 * @author hehua2008
 * @date 2022/3/7
 */
interface PhotoViewerCallback {
    fun addScreenListener(position: Int, listener: OnScreenListener)

    fun removeScreenListener(position: Int)

    fun toggleFullScreen()

    fun getCurrentPosition(): Int
}