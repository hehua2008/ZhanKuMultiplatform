package com.hym.zhankucompose.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.hym.zhankucompose.ui.behavior.HideHeaderOnScrollBehavior
import com.hym.zhankucompose.ui.behavior.ScrollingViewBehavior

/**
 * @author hehua2008
 * @date 2021/12/10
 */
abstract class HeaderLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes),
    CoordinatorLayout.AttachedBehavior {

    override fun getBehavior(): CoordinatorLayout.Behavior<HeaderLayout> {
        return HideHeaderOnScrollBehavior()
    }

    class HeaderScrollingViewBehavior : ScrollingViewBehavior<HeaderLayout> {
        constructor() : super()

        constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

        override fun isDependentView(view: View): Boolean {
            return view is HeaderLayout
        }

        override fun getDependentViewTotalScrollRange(view: HeaderLayout): Int {
            return view.height
        }
    }
}