package com.hym.photoviewer

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

    /**
     * Called when a right-to-left touch move intercept is about to occur.
     *
     * @param origX the raw x coordinate of the initial touch
     * @param origY the raw y coordinate of the initial touch
     * @return `true` if the touch should be intercepted.
     */
    fun onInterceptMoveLeft(origX: Float, origY: Float): Boolean

    /**
     * Called when a left-to-right touch move intercept is about to occur.
     *
     * @param origX the raw x coordinate of the initial touch
     * @param origY the raw y coordinate of the initial touch
     * @return `true` if the touch should be intercepted.
     */
    fun onInterceptMoveRight(origX: Float, origY: Float): Boolean
}