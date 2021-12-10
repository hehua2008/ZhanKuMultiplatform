package com.hym.zhankukotlin.ui.detail

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.hym.zhankukotlin.databinding.DetailHeaderLayoutBinding
import com.hym.zhankukotlin.model.WorkDetails

/**
 * @author hehua2008
 * @date 2021/12/10
 */
class DetailHeaderLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {
    private lateinit var binding: DetailHeaderLayoutBinding

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = DetailHeaderLayoutBinding.bind(this)
    }

    fun setWorkDetails(workDetails: WorkDetails) {
        binding.run {
            downloadAll.visibility =
                if (workDetails.product.productImages.isEmpty()) GONE else VISIBLE
            detailTime.text = workDetails.product.publishTimeDiffStr
            detailViews.text = "${workDetails.product.viewCount}"
            detailComments.text = "${workDetails.product.commentCount}"
            detailFavorites.text = "${workDetails.product.favoriteCount}"
        }
    }
}