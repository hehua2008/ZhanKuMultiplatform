package com.hym.zhankukotlin.ui

import android.view.Choreographer
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView
import com.hym.zhankukotlin.util.getFastScroller
import kotlin.math.abs

/**
 * @author hehua2008
 * @date 2022/3/30
 */
class FastScrollListener(
    private val onPauseResumeCallback: FastScrollCallback,
    private val startFastScrollVelocityInDp: Int = 6000,
    private val stopFastScrollVelocityInDp: Int = 2000
) : RecyclerView.OnScrollListener(), FastScroller.OnDragListener {
    companion object {
        private const val TAG = "FastScrollListener"
    }

    private var mStartFastScrollVelocityInPixel: Float = startFastScrollVelocityInDp.toFloat()
    private var mStopFastScrollVelocityInPixel: Float = stopFastScrollVelocityInDp.toFloat()

    private var mScrolledY = 0F
    private var mLastScrollState = RecyclerView.SCROLL_STATE_IDLE
    private var mLastDragState = FastScroller.DRAG_NONE
    private var mIsFastScrolling = false
    private val mVelocityTracker: VelocityTracker = VelocityTracker.obtain()

    private var mRecyclerView: RecyclerView? = null
    private var mMaxFlingVelocity = ViewConfiguration.getMaximumFlingVelocity().toFloat()

    fun attachToRecyclerView(recyclerView: RecyclerView) {
        detachFromRecyclerView()
        mRecyclerView = recyclerView
        mMaxFlingVelocity = recyclerView.maxFlingVelocity.toFloat()
        recyclerView.context.resources.displayMetrics.density.let {
            mStartFastScrollVelocityInPixel = it * startFastScrollVelocityInDp
            mStopFastScrollVelocityInPixel = it * stopFastScrollVelocityInDp
        }
        recyclerView.addOnScrollListener(this)
        recyclerView.getFastScroller()?.addOnDragStateChangedListener(this)
    }

    fun detachFromRecyclerView() {
        mRecyclerView?.let {
            it.removeOnScrollListener(this)
            it.getFastScroller()?.removeOnDragStateChangedListener(this)
        }
        mScrolledY = 0F
        mLastScrollState = RecyclerView.SCROLL_STATE_IDLE
        mLastDragState = FastScroller.DRAG_NONE
        mIsFastScrolling = false
        mVelocityTracker.clear()
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        mScrolledY += dy
    }

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        if (newState == RecyclerView.SCROLL_STATE_IDLE) {
            mScrolledY = 0F
            mVelocityTracker.clear()
            if (mLastDragState != FastScroller.DRAG_NONE) return
            notifyStopFastScroll()
        } else if (mLastScrollState == RecyclerView.SCROLL_STATE_IDLE) {
            Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
                override fun doFrame(frameTimeNanos: Long) {
                    if (mLastScrollState == RecyclerView.SCROLL_STATE_IDLE
                        || mLastDragState != FastScroller.DRAG_NONE
                    ) return

                    val now = frameTimeNanos / 1000000
                    val ev =
                        MotionEvent.obtain(now, now, MotionEvent.ACTION_MOVE, 0F, mScrolledY, 0)
                    mVelocityTracker.addMovement(ev)
                    ev.recycle()

                    mVelocityTracker.computeCurrentVelocity(
                        1000,
                        mStartFastScrollVelocityInPixel.coerceAtLeast(mMaxFlingVelocity)
                    )
                    val absVelocityY = abs(mVelocityTracker.yVelocity)
                    //Log.w(TAG, "absVelocityY=absVelocityY")
                    if (absVelocityY >= mStartFastScrollVelocityInPixel) {
                        notifyStartFastScroll()
                    } else if (absVelocityY < mStopFastScrollVelocityInPixel) {
                        notifyStopFastScroll()
                    }

                    Choreographer.getInstance().postFrameCallback(this)
                }
            })
        }
        mLastScrollState = newState
    }

    override fun onDragStateChanged(newDragState: Int) {
        if (mLastDragState == newDragState) return
        if (newDragState == FastScroller.DRAG_Y) {
            notifyStartFastScroll()
        } else if (newDragState == FastScroller.DRAG_NONE) {
            notifyStopFastScroll()
        }
        mLastDragState = newDragState
    }

    private fun notifyStartFastScroll() {
        if (mIsFastScrolling) return
        mIsFastScrolling = true
        onPauseResumeCallback.onStartFastScroll()
    }

    private fun notifyStopFastScroll() {
        mIsFastScrolling = false
        onPauseResumeCallback.onStopFastScroll()
    }

    interface FastScrollCallback {
        fun onStartFastScroll()

        fun onStopFastScroll()
    }
}