package com.hym.zhankukotlin.ui.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ImageItemBinding
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.ui.ImageViewLoadingListener

class DetailImageAdapter : RecyclerView.Adapter<BindingViewHolder<ImageItemBinding>>() {
    private var mImgUrls: List<String> = emptyList()

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
        if (!ImageViewLoadingListener.shouldReLoadImage(imageView, url)) {
            return
        }
        imageView.setImageDrawable(MyApplication.transparentDrawable)
        val listener = ImageViewLoadingListener.createListener(imageView, url)
        MyApplication.imageLoader.displayImage(url, listener.imageAware, listener)
    }

    override fun getItemCount(): Int {
        return mImgUrls.size
    }

    override fun onViewRecycled(holder: BindingViewHolder<ImageItemBinding>) {
        val imageView = holder.binding.imageView
        ImageViewLoadingListener.resetImageViewTags(imageView)
        val listener = ImageViewLoadingListener.getListener(imageView)
        if (listener != null) {
            MyApplication.imageLoader.cancelDisplayTask(listener.imageAware)
        }
    }

    fun setImgUrls(imgUrls: List<String>) {
        mImgUrls = imgUrls
        notifyDataSetChanged()
    }
}