package com.hym.zhankukotlin.ui.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout.Behavior
import androidx.core.view.ViewCompat
import java.lang.ref.WeakReference

/**
 * @author hehua2008
 * @date 2021/12/10
 *
 * The [Behavior] for a View within a [CoordinatorLayout] to hide the view off the
 * top of the screen when scrolling up, and show it when scrolling down.
 */
class HideHeaderOnScrollBehavior<V : View> : HeaderBehavior<V> {

    private var lastNestedScrollingChildRef: WeakReference<View>? = null

    constructor()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun onStartNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        directTargetChild: View,
        target: View,
        nestedScrollAxes: Int,
        type: Int
    ): Boolean {
        // Return true if we're nested scrolling vertically, and we either have lift on scroll
        // enabled or we can scroll the children.
        val started = (nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)

        // A new nested scroll has started so clear out the previous ref
        lastNestedScrollingChildRef = null

        return started
    }

    override fun onNestedPreScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dx: Int,
        dy: Int,
        consumed: IntArray,
        type: Int
    ) {
        if (dy == 0) return
        val min: Int
        val max: Int
        if (dy < 0) {
            // We're scrolling down
            min = -child.height // totalScrollRange
            max = min + 0 // child.getDownNestedPreScrollRange()
        } else {
            // We're scrolling up
            min = -child.height // totalScrollRange // getUpNestedPreScrollRange();
            max = 0
        }
        if (min != max) {
            consumed[1] = scroll(coordinatorLayout, child, dy, min, max)
        }
    }

    override fun onNestedScroll(
        coordinatorLayout: CoordinatorLayout,
        child: V,
        target: View,
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        type: Int,
        consumed: IntArray
    ) {
        if (dyUnconsumed >= 0) return
        // If the scrolling view is scrolling down but not consuming, it's probably be at
        // the top of it's content
        consumed[1] = scroll(
            coordinatorLayout, child, dyUnconsumed,
            -child.height /*-child.getDownNestedScrollRange()*/, 0
        )
    }

    override fun onStopNestedScroll(
        coordinatorLayout: CoordinatorLayout, child: V, target: View, type: Int
    ) {
        // Keep a reference to the previous nested scrolling child
        lastNestedScrollingChildRef = WeakReference(target)
    }

    override fun canDragView(view: V): Boolean {
        // Else we'll use the default behaviour of seeing if it can scroll down
        return lastNestedScrollingChildRef?.let {
            // If we have a reference to a scrolling view, check it
            val scrollingView = it.get()
            (scrollingView != null && scrollingView.isShown
                    && !scrollingView.canScrollVertically(-1))
        } ?: true // Otherwise we assume that the scrolling view hasn't been scrolled and can drag.
    }
}