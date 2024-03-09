package com.hym.zhankucompose.ui

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * @author hehua2008
 * @date 2024/3/8
 */
class ComposeViewLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    val composeView = ComposeView(context)
        .also { addView(it) }

    companion object {
        @JvmStatic
        fun createLayoutParams(
            width: Int = ViewGroup.LayoutParams.MATCH_PARENT,
            height: Int = ViewGroup.LayoutParams.MATCH_PARENT
        ): LayoutParams {
            return LayoutParams(width, height)
        }
    }
}
