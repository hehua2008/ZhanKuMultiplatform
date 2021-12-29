package com.hym.zhankukotlin.ui.detail

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.hym.zhankukotlin.databinding.DetailHeaderLayoutBinding
import com.hym.zhankukotlin.model.WorkDetails
import com.hym.zhankukotlin.ui.CircleViewOutlineProvider
import com.hym.zhankukotlin.ui.HeaderLayout
import com.hym.zhankukotlin.ui.author.AuthorItemFragment
import com.hym.zhankukotlin.ui.tag.TagActivity
import com.hym.zhankukotlin.util.ViewUtils.getActivity

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
        binding = DetailHeaderLayoutBinding.bind(this).apply {
            detailAvatar.clipToOutline = true
            detailAvatar.outlineProvider = CircleViewOutlineProvider
        }
    }

    fun setWorkDetails(workDetails: WorkDetails) {
        binding.run {
            detailAuthor.text = workDetails.product.creatorObj.username
            View.OnClickListener { v ->
                val activity = v.getActivity() ?: return@OnClickListener
                val intent = Intent(activity, TagActivity::class.java)
                workDetails.product.creatorObj.run {
                    intent.putExtra(AuthorItemFragment.AUTHOR_UID, id)
                    intent.putExtra(AuthorItemFragment.AUTHOR_NAME, username)
                }
                activity.startActivity(intent)
            }.let {
                authorGroup.setOnClickListener(it)
                detailAuthor.setOnClickListener(it)
            }
            downloadAll.isVisible = workDetails.product.productImages.isNotEmpty()
            detailTime.text = workDetails.product.publishTimeDiffStr
            detailViews.text = "${workDetails.product.viewCount}"
            detailComments.text = "${workDetails.product.commentCount}"
            detailFavorites.text = "${workDetails.product.favoriteCount}"
            workDetails.sharewords.let {
                detailShareWords.isVisible = it.isNotBlank()
                detailShareWords.text = it
            }
        }
    }
}