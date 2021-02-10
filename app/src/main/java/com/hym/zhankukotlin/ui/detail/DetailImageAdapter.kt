package com.hym.zhankukotlin.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.GlideAppExtension
import com.hym.zhankukotlin.GlideRequests
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ImageItemBinding
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.ui.ThemeColorListener

class DetailImageAdapter : RecyclerView.Adapter<BindingViewHolder<ImageItemBinding>>() {
    private var mRequestManager: GlideRequests? = null
    private var mImgUrls: List<String> = emptyList()
    private var mFirstBind = true

    override fun onCreateViewHolder(
            parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ImageItemBinding> {
        val binding: ImageItemBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context), R.layout.image_item, parent, false
        )
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(
            holder: BindingViewHolder<ImageItemBinding>, position: Int
    ) {
        val url = mImgUrls[position]
        val imageView = holder.binding.imageView
        mRequestManager!!
                .load(url)
                .transparentPlaceHolder()
                .transition(GlideAppExtension.DRAWABLE_CROSS_FADE)
                //.originalSize()
                .apply {
                    if (mFirstBind && position == 0) {
                        mFirstBind = false
                        addListener(ThemeColorListener)
                    }
                }
                .into(imageView)
                .waitForLayout()
    }

    override fun onViewRecycled(holder: BindingViewHolder<ImageItemBinding>) {
        mRequestManager?.clear(holder.binding.imageView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = GlideApp.with(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = null
    }

    override fun getItemCount(): Int {
        return mImgUrls.size
    }

    fun setImgUrls(imgUrls: List<String>) {
        mImgUrls = imgUrls
        notifyDataSetChanged()
    }
}