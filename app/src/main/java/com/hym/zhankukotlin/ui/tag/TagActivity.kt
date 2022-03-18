package com.hym.zhankukotlin.ui.tag

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.commitNow
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hym.zhankukotlin.BaseActivity
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.TagActivityBinding
import com.hym.zhankukotlin.model.CreatorObj
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.ui.author.AuthorItemFragment
import com.hym.zhankukotlin.ui.main.PreviewItemFragment

class TagActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val author: CreatorObj? = intent.getParcelableExtra(AuthorItemFragment.AUTHOR)
        val topCate: TopCate? = intent.getParcelableExtra(PreviewItemFragment.TOP_CATE)
        val subCate: SubCate? = intent.getParcelableExtra(PreviewItemFragment.SUB_CATE)
        val mTitle = author?.username ?: topCate?.name ?: subCate?.name

        val binding = TagActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.actionBar.run {
            title = mTitle
            author?.let {
                GlideApp.with(this@TagActivity)
                    .load(it.avatar1x)
                    .optionalCircleCrop()
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable, transition: Transition<in Drawable>?
                        ) {
                            logo = resource
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            logo = null
                        }
                    })
            }
            setNavigationOnClickListener { finish() }
        }

        if (savedInstanceState != null) return
        supportFragmentManager.commitNow {
            if (author != null) {
                replace(R.id.container, AuthorItemFragment.newInstance(author))
            } else {
                replace(R.id.container, PreviewItemFragment.newInstance(topCate, subCate))
            }
        }
    }
}