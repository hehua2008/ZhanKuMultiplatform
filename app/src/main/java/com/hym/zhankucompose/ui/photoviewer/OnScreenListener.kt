package com.hym.zhankucompose.ui.photoviewer

/**
 * @author hehua2008
 * @date 2022/3/7
 */
/**
 * Listener to be invoked for screen events.
 */
interface OnScreenListener {
    /**
     * The full screen state has changed.
     */
    fun onFullScreenChanged(fullScreen: Boolean)

    /**
     * A new fragment has been activated and the previous fragment de-activated.
     */
    fun onFragmentActivated()
}