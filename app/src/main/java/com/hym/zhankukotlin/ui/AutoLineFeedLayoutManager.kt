package com.hym.zhankukotlin.ui

import android.content.Context
import android.util.AttributeSet
import android.util.Size
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler

class AutoLineFeedLayoutManager : LinearLayoutManager {
    constructor(context: Context?) : super(context) {
        //setOrientation(RecyclerView.HORIZONTAL)
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes) {
        //setOrientation(RecyclerView.HORIZONTAL)
    }

    override fun generateDefaultLayoutParams(): RecyclerView.LayoutParams {
        return RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.WRAP_CONTENT, RecyclerView.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        onAverageLayoutChildren(recycler, state)
    }

    private fun onLeftLayoutChildren(recycler: Recycler, state: RecyclerView.State?) {
        detachAndScrapAttachedViews(recycler)

        val parentWidth = width
        var curLineWidthSum = 0
        var curLineTop = 0
        var lastLineMaxHeight = 0
        var i = 0
        val count = itemCount

        while (i < count) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val width = getDecoratedMeasuredWidth(view)
            val height = getDecoratedMeasuredHeight(view)
            curLineWidthSum += width

            if (curLineWidthSum <= parentWidth) {
                layoutDecorated(
                    view,
                    curLineWidthSum - width, curLineTop, curLineWidthSum, curLineTop + height
                )
                if (lastLineMaxHeight < height) {
                    lastLineMaxHeight = height
                }
            } else {
                curLineWidthSum = width
                if (lastLineMaxHeight == 0) {
                    lastLineMaxHeight = height
                }
                curLineTop += lastLineMaxHeight
                layoutDecorated(view, 0, curLineTop, width, curLineTop + height)
                lastLineMaxHeight = height
            }

            i++
        }
    }

    private fun onAverageLayoutChildren(recycler: Recycler, state: RecyclerView.State) {
        detachAndScrapAttachedViews(recycler)

        val parentWidth = width
        var curLineWidthSum = 0
        var curLineTop = 0
        var lastLineMaxHeight = 0
        val lastLineMap: MutableMap<View, Size> = LinkedHashMap()
        var i = 0
        val count = itemCount

        while (i < count) {
            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)
            val width = getDecoratedMeasuredWidth(view)
            val height = getDecoratedMeasuredHeight(view)
            curLineWidthSum += width
            if (curLineWidthSum <= parentWidth) {
                lastLineMap[view] = Size(width, height)
                if (lastLineMaxHeight < height) {
                    lastLineMaxHeight = height
                }
            } else {
                val lastLineSize = lastLineMap.size
                if (lastLineSize > 0) {
                    val widthOffset = (parentWidth - curLineWidthSum + width) / (lastLineSize + 1)
                    var idxLeft = widthOffset
                    for ((idxView, idxSize) in lastLineMap) {
                        val idxRight = idxLeft + idxSize.width
                        layoutDecorated(
                            idxView,
                            idxLeft, curLineTop, idxRight, curLineTop + idxSize.height
                        )
                        idxLeft = idxRight + widthOffset
                    }
                    lastLineMap.clear()
                }
                lastLineMap[view] = Size(width, height)
                curLineWidthSum = width
                if (lastLineMaxHeight == 0) {
                    lastLineMaxHeight = height
                }
                curLineTop += lastLineMaxHeight
                lastLineMaxHeight = height
            }

            i++
        }

        val lastLineSize = lastLineMap.size
        if (lastLineSize > 0) {
            val widthOffset = (parentWidth - curLineWidthSum) / (lastLineSize + 1)
            var idxLeft = widthOffset
            for ((idxView, idxSize) in lastLineMap) {
                val idxRight = idxLeft + idxSize.width
                layoutDecorated(
                    idxView,
                    idxLeft, curLineTop, idxRight, curLineTop + idxSize.height
                )
                idxLeft = idxRight + widthOffset
            }
        }
        lastLineMap.clear()
    }
}