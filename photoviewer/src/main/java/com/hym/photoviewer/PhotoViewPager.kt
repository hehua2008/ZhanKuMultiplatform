package com.hym.photoviewer

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager

/**
 * View pager for photo view fragments. Define our own class so we can specify the view pager in
 * XML.
 */
class PhotoViewPager @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ViewPager(context, attrs) {
    companion object {
        private const val INVALID_POINTER = -1
    }

    /**
     * A type of intercept that should be performed
     */
    enum class InterceptType {
        NONE, LEFT, RIGHT, BOTH
    }

    /**
     * Provides an ability to intercept touch events.
     *
     * [ViewPager] intercepts all touch events and we need to be able to override this behavior.
     * Instead, we could perform a similar function by declaring a custom [ViewGroup] to contain
     * the pager and intercept touch events at a higher level.
     */
    interface OnInterceptTouchListener {
        /**
         * Called when a touch intercept is about to occur.
         *
         * @param origX the raw x coordinate of the initial touch
         * @param origY the raw y coordinate of the initial touch
         * @return Which type of touch, if any, should should be intercepted.
         */
        fun onTouchIntercept(origX: Float, origY: Float): InterceptType
    }

    /*
    init {
        // Set the page transformer to perform the transition animation
        // for each page in the view.
        setPageTransformer(true) { page, position ->
            // The >= 1 is needed so that the page
            // (page A) that transitions behind the newly visible
            // page (page B) that comes in from the left does not
            // get the touch events because it is still on screen
            // (page A is still technically on screen despite being
            // invisible). This makes sure that when the transition
            // has completely finished, we revert it to its default
            // behavior and move it off of the screen.
            if (position < 0 || position >= 1f) {
                page.translationX = 0f
                page.alpha = 1f
                page.scaleX = 1f
                page.scaleY = 1f
            } else {
                page.translationX = -position * page.width
                page.alpha = Math.max(0f, 1f - position)
                val scale = Math.max(0f, 1f - position * 0.3f)
                page.scaleX = scale
                page.scaleY = scale
            }
        }
    }
    */

    private var mLastMotionX = 0f

    private var mActivePointerId = 0

    /**
     * The x coordinate where the touch originated
     */
    private var mActivatedX = 0f

    /**
     * The y coordinate where the touch originated
     */
    private var mActivatedY = 0f

    private var mListener: OnInterceptTouchListener? = null

    /**
     * We intercept touch event intercepts so we can prevent switching views when the current view
     * is internally scrollable.
     */
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        val intercept = mListener?.onTouchIntercept(mActivatedX, mActivatedY) ?: InterceptType.NONE
        val ignoreScrollLeft = intercept == InterceptType.BOTH || intercept == InterceptType.LEFT
        val ignoreScrollRight = intercept == InterceptType.BOTH || intercept == InterceptType.RIGHT

        // Only check ability to page if we can't scroll in one / both directions
        val action = ev.action and MotionEvent.ACTION_MASK
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mActivePointerId = INVALID_POINTER
        }
        when (action) {
            MotionEvent.ACTION_MOVE -> {
                if (ignoreScrollLeft || ignoreScrollRight) {
                    val activePointerId = mActivePointerId
                    if (activePointerId == INVALID_POINTER) {
                        // If we don't have a valid id, the touch down wasn't on content.
                    } else {
                        val pointerIndex = ev.findPointerIndex(activePointerId)
                        val x = ev.getX(pointerIndex)
                        if (ignoreScrollLeft && ignoreScrollRight) {
                            mLastMotionX = x
                            return false
                        } else if (ignoreScrollLeft && x > mLastMotionX) {
                            mLastMotionX = x
                            return false
                        } else if (ignoreScrollRight && x < mLastMotionX) {
                            mLastMotionX = x
                            return false
                        }
                    }
                }
            }
            MotionEvent.ACTION_DOWN -> {
                mLastMotionX = ev.x
                // Use the raw x/y as the children can be located anywhere and there isn't a
                // single offset that would be meaningful
                mActivatedX = ev.rawX
                mActivatedY = ev.rawY
                mActivePointerId = ev.getPointerId(0)
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.actionIndex
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    // Our active pointer going up; select a new active pointer
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastMotionX = ev.getX(newPointerIndex)
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    /**
     * sets the intercept touch listener.
     */
    fun setOnInterceptTouchListener(listener: OnInterceptTouchListener?) {
        mListener = listener
    }
}