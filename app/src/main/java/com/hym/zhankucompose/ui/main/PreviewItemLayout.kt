package com.hym.zhankucompose.ui.main

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.hym.zhankucompose.databinding.PreviewItemLayoutBinding
import com.hym.zhankucompose.model.Content
import com.hym.zhankucompose.ui.CircleViewOutlineProvider
import com.hym.zhankucompose.util.copyText

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
        binding = PreviewItemLayoutBinding.bind(this).apply {
            avatar.clipToOutline = true
            avatar.outlineProvider = CircleViewOutlineProvider
            View.OnLongClickListener { v ->
                v.copyText()
            }.let {
                author.setOnLongClickListener(it)
                description.setOnLongClickListener(it)
            }
        }
    }

    fun setContent(content: Content) {
        binding.run {
            previewImg.contentDescription = content.cover
            author.text = content.creatorObj.username
            description.text = content.formatTitle
            time.text = content.updateTimeStr
            viewCount.text = content.viewCountStr
            comments.text = content.commentCountStr
        }
    }
}