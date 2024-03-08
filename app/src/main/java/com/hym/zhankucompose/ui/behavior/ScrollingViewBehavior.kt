package com.hym.zhankucompose.ui.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat

/**
 * Behavior which should be used by [View]s which can scroll vertically and support nested
 * scrolling to automatically scroll dependent view sibling.
 *
 * @author hehua2008
 * @date 2021/12/10
 */
abstract class ScrollingViewBehavior<V : View> : HeaderScrollingViewBehavior {

    abstract fun isDependentView(view: View): Boolean

    /**
     * Returns the scroll range of all children of dependent view.
     *
     * @return the scroll range in px
     */
    abstract fun getDependentViewTotalScrollRange(view: V): Int

    constructor()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun layoutDependsOn(
        parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        // We depend on dependent view
        return isDependentView(dependency)
    }

    override fun onDependentViewChanged(
        parent: CoordinatorLayout, child: View, dependency: View
    ): Boolean {
        offsetChildAsNeeded(child, dependency)
        return false
    }

    override fun onDependentViewRemoved(parent: CoordinatorLayout, child: View, dependency: View) {
        if (!isDependentView(dependency)) return
        ViewCompat.removeAccessibilityAction(
            parent,
            AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_FORWARD.id
        )
        ViewCompat.removeAccessibilityAction(
            parent,
            AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_SCROLL_BACKWARD.id
        )
    }

    private fun offsetChildAsNeeded(child: View, dependency: View) {
        // Offset the child, pinning it to the bottom the header-dependency, maintaining
        // any vertical gap and overlap
        ViewCompat.offsetTopAndBottom(
            child, dependency.bottom - child.top
                    + verticalLayoutGap
                    - getOverlapPixelsForOffset()
        )
    }

    override fun findFirstDependency(views: List<View>): V? {
        var i = 0
        val z = views.size
        while (i < z) {
            val view = views[i]
            if (isDependentView(view)) {
                return view as V
            }
            i++
        }
        return null
    }

    override fun getScrollRange(v: View): Int {
        return if (isDependentView(v)) {
            getDependentViewTotalScrollRange(v as V)
        } else {
            super.getScrollRange(v)
        }
    }
}