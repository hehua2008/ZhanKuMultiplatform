package com.hym.zhankukotlin.ui.photoview

/**
 * @author hehua2008
 * @date 2022/3/7
 *
 * Interface for components that are internally scrollable left-to-right.
 */
interface HorizontallyScrollable {
    /**
     * Return `true` if the component needs to receive right-to-left
     * touch movements.
     *
     * @param origX the raw x coordinate of the initial touch
     * @param origY the raw y coordinate of the initial touch
     */
    fun interceptMoveLeft(origX: Float, origY: Float): Boolean

    /**
     * Return `true` if the component needs to receive left-to-right
     * touch movements.
     *
     * @param origX the raw x coordinate of the initial touch
     * @param origY the raw y coordinate of the initial touch
     */
    fun interceptMoveRight(origX: Float, origY: Float): Boolean
}