package com.hym.zhankukotlin.ui.main

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.hym.zhankukotlin.databinding.PreviewHeaderLayoutBinding

/**
 * @author hehua2008
 * @date 2021/12/10
 */
class PreviewHeaderLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var binding: PreviewHeaderLayoutBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = PreviewHeaderLayoutBinding.bind(this)
    }
}