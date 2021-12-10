package com.hym.zhankukotlin.ui.behavior

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Behavior will automatically sets up a [ViewOffsetHelper] on a [View].
 *
 * @author hehua2008
 * @date 2021/12/10
 */
open class ViewOffsetBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    constructor()

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    private var viewOffsetHelper: ViewOffsetHelper? = null
    private var tempTopBottomOffset = 0
    private var tempLeftRightOffset = 0

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        // First let lay the child out
        layoutChild(parent, child, layoutDirection)
        if (viewOffsetHelper == null) {
            viewOffsetHelper = ViewOffsetHelper(child)
        }
        viewOffsetHelper?.onViewLayout()
        viewOffsetHelper?.applyOffsets()
        if (tempTopBottomOffset != 0) {
            viewOffsetHelper?.setTopAndBottomOffset(tempTopBottomOffset)
            tempTopBottomOffset = 0
        }
        if (tempLeftRightOffset != 0) {
            viewOffsetHelper?.setLeftAndRightOffset(tempLeftRightOffset)
            tempLeftRightOffset = 0
        }
        return true
    }

    protected open fun layoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int) {
        // Let the parent lay it out by default
        parent.onLayoutChild(child, layoutDirection)
    }

    fun setTopAndBottomOffset(offset: Int): Boolean {
        tempTopBottomOffset = viewOffsetHelper?.let {
            return it.setTopAndBottomOffset(offset)
        } ?: offset
        return false
    }

    fun setLeftAndRightOffset(offset: Int): Boolean {
        tempLeftRightOffset = viewOffsetHelper?.let {
            return it.setLeftAndRightOffset(offset)
        } ?: offset
        return false
    }

    val topAndBottomOffset: Int
        get() = viewOffsetHelper?.topAndBottomOffset ?: 0

    val leftAndRightOffset: Int
        get() = viewOffsetHelper?.leftAndRightOffset ?: 0

    var isVerticalOffsetEnabled: Boolean
        get() = viewOffsetHelper?.isVerticalOffsetEnabled ?: false
        set(value) {
            viewOffsetHelper?.isVerticalOffsetEnabled = value
        }

    var isHorizontalOffsetEnabled: Boolean
        get() = viewOffsetHelper?.isHorizontalOffsetEnabled ?: false
        set(value) {
            viewOffsetHelper?.isHorizontalOffsetEnabled = value
        }
}