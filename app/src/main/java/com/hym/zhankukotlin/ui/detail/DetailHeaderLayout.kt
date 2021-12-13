package com.hym.zhankukotlin.ui.detail

import android.content.Context
import android.util.AttributeSet
import androidx.core.view.isVisible
import com.hym.zhankukotlin.databinding.DetailHeaderLayoutBinding
import com.hym.zhankukotlin.model.WorkDetails
import com.hym.zhankukotlin.ui.HeaderLayout

/**
 * @author hehua2008
 * @date 2021/12/10
 */
class DetailHeaderLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : HeaderLayout(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var binding: DetailHeaderLayoutBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = DetailHeaderLayoutBinding.bind(this)
    }

    fun setWorkDetails(workDetails: WorkDetails) {
        binding.run {
            downloadAll.isVisible = workDetails.product.productImages.isNotEmpty()
            detailTime.text = workDetails.product.publishTimeDiffStr
            detailViews.text = "${workDetails.product.viewCount}"
            detailComments.text = "${workDetails.product.commentCount}"
            detailFavorites.text = "${workDetails.product.favoriteCount}"
        }
    }
}