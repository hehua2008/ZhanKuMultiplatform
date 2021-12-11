package com.hym.zhankukotlin.ui.main

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.hym.zhankukotlin.databinding.PreviewItemLayoutBinding
import com.hym.zhankukotlin.model.Content

/**
 * @author hehua2008
 * @date 2021/12/10
 */
class PreviewItemLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var binding: PreviewItemLayoutBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = PreviewItemLayoutBinding.bind(this)
    }

    fun setContent(content: Content) {
        binding.run {
            previewImg.contentDescription = content.cover
            author.text = content.creatorObj.username
            description.text = content.formatTitle
            time.text = content.publishTimeDiffStr
            viewCount.text = content.viewCountStr
            comments.text = content.commentCountStr
        }
    }
}