package com.hym.zhankukotlin.ui

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.core.view.ViewCompat
import androidx.core.view.ViewParentCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlin.math.min

class AutoScrollGridLayoutManager : GridLayoutManager, View.OnLayoutChangeListener {
    protected var mRecyclerView: RecyclerView? = null
    private var mForceScrolling = false

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
    }

    constructor(context: Context?, spanCount: Int) : super(context, spanCount) {}

    constructor(context: Context?, spanCount: Int, orientation: Int, reverseLayout: Boolean)
            : super(context, spanCount, orientation, reverseLayout) {
    }

    @CallSuper
    override fun onAttachedToWindow(view: RecyclerView) {
        super.onAttachedToWindow(view)
        mRecyclerView = view
        mRecyclerView!!.addOnLayoutChangeListener(this)
    }

    @CallSuper
    override fun onDetachedFromWindow(view: RecyclerView, recycler: Recycler) {
        super.onDetachedFromWindow(view, recycler)
        mRecyclerView!!.removeOnLayoutChangeListener(this)
        mRecyclerView = null
    }

    @CallSuper
    override fun scrollVerticallyBy(dy: Int, recycler: Recycler, state: RecyclerView.State): Int {
        if (dy == 0) {
            return super.scrollVerticallyBy(dy, recycler, state)
        } else if (mForceScrolling) {
            super.scrollVerticallyBy(-dy, recycler, state)
            mForceScrolling = false
            return 0
        }
        val parentView = getParentNestedScrollView(mRecyclerView)
                ?: return super.scrollVerticallyBy(dy, recycler, state)
        val parentVisibleRect = Rect()
        parentView.getGlobalVisibleRect(parentVisibleRect)
        val recyclerVisibleRect = Rect()
        mRecyclerView!!.getGlobalVisibleRect(recyclerVisibleRect)
        return if (dy > 0) {
            val verticalOffset = recyclerVisibleRect.top - parentVisibleRect.top
            if (verticalOffset <= 0) {
                super.scrollVerticallyBy(dy, recycler, state)
            } else { // verticalOffset > 0
                if (dy <= verticalOffset) {
                    0
                } else { // dy > verticalOffset
                    super.scrollVerticallyBy(dy - verticalOffset, recycler, state)
                }
            }
        } else {
            val verticalOffset = recyclerVisibleRect.bottom - parentVisibleRect.bottom
            if (verticalOffset >= 0) {
                super.scrollVerticallyBy(dy, recycler, state)
            } else { // verticalOffset < 0
                if (dy >= verticalOffset) {
                    0
                } else { // dy < verticalOffset
                    super.scrollVerticallyBy(dy - verticalOffset, recycler, state)
                }
            }
        }
    }

    @CallSuper
    override fun onLayoutChange(
            v: View, left: Int, top: Int, right: Int, bottom: Int,
            oldLeft: Int, oldTop: Int, oldRight: Int, oldBottom: Int
    ) {
        mForceScrolling = false
        val parentView = getParentNestedScrollView(mRecyclerView) ?: return
        val offset = mRecyclerView!!.computeVerticalScrollOffset()
        val extent = mRecyclerView!!.computeVerticalScrollExtent()
        val range = mRecyclerView!!.computeVerticalScrollRange()
        val bottomRemain = range - offset - extent
        if (bottomRemain <= 0) {
            return
        }
        val parentVisibleRect = Rect()
        parentView.getGlobalVisibleRect(parentVisibleRect)
        val recyclerVisibleRect = Rect()
        mRecyclerView!!.getGlobalVisibleRect(recyclerVisibleRect)
        val bottomCovered = parentVisibleRect.bottom - recyclerVisibleRect.bottom
        if (bottomCovered > 0) {
            mForceScrolling = true
            mRecyclerView!!.startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL)
            mRecyclerView!!.scrollBy(0, -min(bottomRemain, bottomCovered))
        }
    }

    companion object {
        @JvmStatic
        fun getParentNestedScrollView(recyclerView: RecyclerView?): ViewGroup? {
            if (recyclerView === null || !recyclerView.isNestedScrollingEnabled) {
                return null
            }
            val axes = ViewCompat.SCROLL_AXIS_VERTICAL
            var p = recyclerView.parent
            var child: View? = recyclerView
            while (p != null) {
                if (p !is SwipeRefreshLayout
                        && ViewParentCompat.onStartNestedScroll(p, child, recyclerView, axes)
                ) {
                    return if (p is ViewGroup) p else null
                }
                if (p is View) {
                    child = p
                }
                p = p.parent
            }
            return null
        }
    }
}