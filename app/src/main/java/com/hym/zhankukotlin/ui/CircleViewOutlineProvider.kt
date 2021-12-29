package com.hym.zhankukotlin.ui

import android.graphics.Outline
import android.view.View
import android.view.ViewOutlineProvider

/**
 * @author hehua2008
 * @date 2021/12/29
 */
object CircleViewOutlineProvider : ViewOutlineProvider() {
    override fun getOutline(view: View, outline: Outline) {
        val min = view.width.coerceAtMost(view.height)
        outline.setOval(0, 0, min, min)
    }
}