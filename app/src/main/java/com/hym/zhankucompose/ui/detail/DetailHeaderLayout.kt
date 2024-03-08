package com.hym.zhankucompose.ui.detail

import android.content.Context
import android.content.Intent
import android.util.AttributeSet
import android.view.View
import androidx.core.view.isVisible
import com.hym.zhankucompose.databinding.DetailHeaderLayoutBinding
import com.hym.zhankucompose.model.ArticleDetails
import com.hym.zhankucompose.model.ProductImage
import com.hym.zhankucompose.model.WorkDetails
import com.hym.zhankucompose.ui.CircleViewOutlineProvider
import com.hym.zhankucompose.ui.HeaderLayout
import com.hym.zhankucompose.ui.author.AuthorItemFragment
import com.hym.zhankucompose.ui.tag.TagActivity
import com.hym.zhankucompose.util.copyText

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
            detailAuthor.setOnLongClickListener { v ->
                v.copyText()
            }
        }
    }

    fun setWorkDetails(workDetails: WorkDetails) {
        binding.run {
            detailAuthor.text = workDetails.product.creatorObj.username
            View.OnClickListener { v ->
                val context = v.context
                val intent = Intent(context, TagActivity::class.java)
                    .putExtra(AuthorItemFragment.AUTHOR, workDetails.product.creatorObj)
                context.startActivity(intent)
            }.let {
                authorGroup.setOnClickListener(it)
                detailAuthor.setOnClickListener(it)
            }
            downloadAll.isVisible = workDetails.product.productImages.isNotEmpty()
            detailTime.text = workDetails.product.updateTimeStr
            detailViews.text = "${workDetails.product.viewCount}"
            detailComments.text = "${workDetails.product.commentCount}"
            detailFavorites.text = "${workDetails.product.favoriteCount}"
            workDetails.sharewords.let {
                detailShareWords.isVisible = it.isNotBlank()
                detailShareWords.text = it
            }
        }
    }

    fun setArticleDetails(articleDetails: ArticleDetails, images: List<ProductImage>) {
        binding.run {
            detailAuthor.text = articleDetails.articledata.creatorObj.username
            View.OnClickListener { v ->
                val context = v.context
                val intent = Intent(context, TagActivity::class.java)
                    .putExtra(AuthorItemFragment.AUTHOR, articleDetails.articledata.creatorObj)
                context.startActivity(intent)
            }.let {
                authorGroup.setOnClickListener(it)
                detailAuthor.setOnClickListener(it)
            }
            downloadAll.isVisible = images.isNotEmpty()
            detailTime.text = articleDetails.articledata.updateTimeStr
            detailViews.text = "${articleDetails.articledata.viewCount}"
            detailComments.text = "${articleDetails.articledata.commentCount}"
            detailFavorites.text = "${articleDetails.articledata.favoriteCount}"
            articleDetails.articledata.summary.let {
                detailShareWords.isVisible = it.isNotBlank()
                detailShareWords.text = it
            }
        }
    }
}