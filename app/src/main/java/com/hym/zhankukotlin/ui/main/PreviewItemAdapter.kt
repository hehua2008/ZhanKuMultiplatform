package com.hym.zhankukotlin.ui.main

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.google.android.flexbox.FlexboxLayoutManager
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.PreviewItemBinding
import com.hym.zhankukotlin.network.PreviewItem
import com.hym.zhankukotlin.network.PreviewResult
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.ui.ImageViewLoadingListener
import com.hym.zhankukotlin.ui.detail.DetailActivity

class PreviewItemAdapter : RecyclerView.Adapter<BindingViewHolder<PreviewItemBinding>>() {
    companion object {
        @JvmStatic
        val previewRecyclerPool = RecycledViewPool()
        protected const val BUTTON_ITEM_TYPE = 1

        init {
            previewRecyclerPool.setMaxRecycledViews(BUTTON_ITEM_TYPE, 20)
        }
    }

    private var mPreviewItems: List<PreviewItem> = emptyList()
    private var mItemIds = LongArray(0)
    override fun getItemViewType(position: Int): Int {
        return BUTTON_ITEM_TYPE
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<PreviewItemBinding> {
        val binding: PreviewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context), R.layout.preview_item, parent, false
        )
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BindingViewHolder<PreviewItemBinding>, position: Int) {
        val previewItem = mPreviewItems[position]
        val imageUrl = previewItem.imageUrl ?: return
        val binding = holder.binding
        binding.previewItem = previewItem
        binding.previewImg.setOnClickListener { v ->
            val context = v.context
            val intent = Intent(context, DetailActivity::class.java)
                .putExtra(DetailActivity.KEY_TITLE, previewItem.title)
                .putExtra(DetailActivity.KEY_URL, previewItem.targetUrl)
            context.startActivity(intent)
        }
        if (!ImageViewLoadingListener.shouldReLoadImage(binding.previewImg, imageUrl)) {
            return
        }
        binding.previewImg.setImageDrawable(null)
        val listener = ImageViewLoadingListener.createListener(binding.previewImg, imageUrl)
        MyApplication.imageLoader.displayImage(previewItem.imageUrl, listener.imageAware, listener)
    }

    override fun getItemCount(): Int {
        return mPreviewItems.size
    }

    override fun getItemId(position: Int): Long {
        return mItemIds[position]
    }

    override fun onViewRecycled(holder: BindingViewHolder<PreviewItemBinding>) {
        val imageView = holder.binding.previewImg
        ImageViewLoadingListener.resetImageViewTags(imageView)
        val listener = ImageViewLoadingListener.getListener(imageView)
        if (listener != null) {
            MyApplication.imageLoader.cancelDisplayTask(listener.imageAware)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setRecycledViewPool(previewRecyclerPool)
        when (val layoutManager = recyclerView.layoutManager) {
            is LinearLayoutManager -> layoutManager.recycleChildrenOnDetach = true
            is FlexboxLayoutManager -> layoutManager.recycleChildrenOnDetach = true
        }
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setRecycledViewPool(null)
    }

    fun setPreviewItems(previewResult: PreviewResult) {
        val lastSize = /*if (previewResult.appendPreviewItems) mPreviewItems.size else*/ 0
        mPreviewItems = previewResult.previewItems
        val newSize = mPreviewItems.size
        val itemIds = LongArray(newSize)
        System.arraycopy(mItemIds, 0, itemIds, 0, lastSize)
        mItemIds = itemIds
        for (i in lastSize until newSize) {
            val previewItem = mPreviewItems[i]
            val urlHash = previewItem.imageUrl.hashCode().toLong()
            val titleHash = previewItem.title.hashCode().toLong()
            mItemIds[i] = urlHash shl 32 or titleHash
        }
        if (/*previewResult.appendPreviewItems*/ false) {
            notifyItemRangeInserted(lastSize, newSize - lastSize)
        } else {
            notifyDataSetChanged()
        }
    }

    init {
        setHasStableIds(true)
    }
}