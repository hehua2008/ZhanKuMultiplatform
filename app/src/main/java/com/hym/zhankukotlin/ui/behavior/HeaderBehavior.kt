package com.hym.zhankukotlin.ui.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.View
import android.view.ViewConfiguration
import android.widget.OverScroller
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import androidx.core.math.MathUtils
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * The [Behavior] for a view that sits vertically above scrolling a view.
 * See [HeaderScrollingViewBehavior].
 *
 * @author hehua2008
 * @date 2021/12/10
 */
abstract class HeaderBehavior<V : View> : ViewOffsetBehavior<V> {
    companion object {
        private const val INVALID_POINTER = -1
    }

    constructor()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var flingRunnable: Runnable? = null
    var scroller: OverScroller? = null
    private var isBeingDragged = false
    private var activePointerId = INVALID_POINTER
    private var lastMotionY = 0
    private var touchSlop = -1
    private var velocityTracker: VelocityTracker? = null

    override fun onInterceptTouchEvent(
        parent: CoordinatorLayout, child: V, ev: MotionEvent
    ): Boolean {
        if (touchSlop < 0) {
            touchSlop = ViewConfiguration.get(parent.context).scaledTouchSlop
        }

        // Shortcut since we're being dragged
        if (ev.actionMasked == MotionEvent.ACTION_MOVE && isBeingDragged) {
            if (activePointerId == INVALID_POINTER) {
                // If we don't have a valid id, the touch down wasn't on content.
                return false
            }
            val pointerIndex = ev.findPointerIndex(activePointerId)
            if (pointerIndex == -1) {
                return false
            }
            val y = ev.getY(pointerIndex).toInt()
            val yDiff = abs(y - lastMotionY)
            if (yDiff > touchSlop) {
                lastMotionY = y
                return true
            }
        }
        if (ev.actionMasked == MotionEvent.ACTION_DOWN) {
            activePointerId = INVALID_POINTER
            val x = ev.x.toInt()
            val y = ev.y.toInt()
            isBeingDragged = canDragView(child) && parent.isPointInChildBounds(child, x, y)
            if (isBeingDragged) {
                lastMotionY = y
                activePointerId = ev.getPointerId(0)
                ensureVelocityTracker()

                // There is an animation in progress. Stop it and catch the view.
                scroller?.let {
                    if (!it.isFinished) {
                        it.abortAnimation()
                        return true
                    }
                }
            }
        }
        velocityTracker?.addMovement(ev)
        return false
    }

    override fun onTouchEvent(parent: CoordinatorLayout, child: V, ev: MotionEvent): Boolean {
        var consumeUp = false
        when (ev.actionMasked) {
            MotionEvent.ACTION_MOVE -> {
                val activePointerIndex = ev.findPointerIndex(activePointerId)
                if (activePointerIndex == -1) {
                    return false
                }
                val y = ev.getY(activePointerIndex).toInt()
                val dy = lastMotionY - y
                lastMotionY = y
                // We're being dragged so scroll the ABL
                scroll(parent, child, dy, getMaxDragOffset(child), 0)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val newIndex = if (ev.actionIndex == 0) 1 else 0
                activePointerId = ev.getPointerId(newIndex)
                lastMotionY = (ev.getY(newIndex) + 0.5f).toInt()
            }
            MotionEvent.ACTION_UP -> {
                velocityTracker?.let {
                    consumeUp = true
                    it.addMovement(ev)
                    it.computeCurrentVelocity(1000)
                    val yvel = it.getYVelocity(activePointerId)
                    fling(parent, child, -getScrollRangeForDragFling(child), 0, yvel)
                }
                isBeingDragged = false
                activePointerId = INVALID_POINTER
                velocityTracker?.let {
                    it.recycle()
                    velocityTracker = null
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                isBeingDragged = false
                activePointerId = INVALID_POINTER
                velocityTracker?.let {
                    it.recycle()
                    velocityTracker = null
                }
            }
        }
        velocityTracker?.addMovement(ev)
        return isBeingDragged || consumeUp
    }

    fun setHeaderTopBottomOffset(parent: CoordinatorLayout, header: V, newOffset: Int): Int {
        return setHeaderTopBottomOffset(parent, header, newOffset, Int.MIN_VALUE, Int.MAX_VALUE)
    }

    protected open fun setHeaderTopBottomOffset(
        parent: CoordinatorLayout, header: V, newOffset: Int, minOffset: Int, maxOffset: Int
    ): Int {
        var newOffset = newOffset
        val curOffset: Int = topAndBottomOffset
        var consumed = 0
        if (minOffset != 0 && curOffset >= minOffset && curOffset <= maxOffset) {
            // If we have some scrolling range, and we're currently within the min and max
            // offsets, calculate a new offset
            newOffset = MathUtils.clamp(newOffset, minOffset, maxOffset)
            if (curOffset != newOffset) {
                setTopAndBottomOffset(newOffset)
                // Update how much dy we have consumed
                consumed = curOffset - newOffset
            }
        }
        return consumed
    }

    protected open fun getTopBottomOffsetForScrollingSibling(): Int {
        return topAndBottomOffset
    }

    fun scroll(
        coordinatorLayout: CoordinatorLayout, header: V, dy: Int, minOffset: Int, maxOffset: Int
    ): Int {
        return setHeaderTopBottomOffset(
            coordinatorLayout,
            header,
            getTopBottomOffsetForScrollingSibling() - dy,
            minOffset,
            maxOffset
        )
    }

    fun fling(
        coordinatorLayout: CoordinatorLayout,
        layout: V,
        minOffset: Int,
        maxOffset: Int,
        velocityY: Float
    ): Boolean {
        if (flingRunnable != null) {
            layout.removeCallbacks(flingRunnable)
            flingRunnable = null
        }
        if (scroller == null) {
            scroller = OverScroller(layout.context)
        }
        scroller?.fling(
            0,
            topAndBottomOffset,  // curr
            0,
            velocityY.roundToInt(),  // velocity.
            0,
            0,  // x
            minOffset,
            maxOffset
        ) // y
        return if (scroller!!.computeScrollOffset()) {
            flingRunnable = FlingRunnable(coordinatorLayout, layout).also {
                ViewCompat.postOnAnimation(layout, it)
            }
            true
        } else {
            onFlingFinished(coordinatorLayout, layout)
            false
        }
    }

    /**
     * Called when a fling has finished, or the fling was initiated but there wasn't enough velocity
     * to start it.
     */
    protected open fun onFlingFinished(parent: CoordinatorLayout, layout: V) {
        // no-op
    }

    /**
     * Return true if the view can be dragged.
     */
    protected open fun canDragView(view: V): Boolean {
        return false
    }

    /**
     * Returns the maximum px offset when `view` is being dragged.
     */
    protected open fun getMaxDragOffset(view: V): Int {
        return -view.height
    }

    protected open fun getScrollRangeForDragFling(view: V): Int {
        return view.height
    }

    private fun ensureVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain()
        }
    }

    private inner class FlingRunnable constructor(
        private val parent: CoordinatorLayout, private val layout: V
    ) : Runnable {
        override fun run() {
            scroller?.let {
                if (it.computeScrollOffset()) {
                    setHeaderTopBottomOffset(parent, layout, it.currY)
                    // Post ourselves so that we run on the next animation
                    ViewCompat.postOnAnimation(layout, this)
                } else {
                    onFlingFinished(parent, layout)
                }
            }
        }
    }
}