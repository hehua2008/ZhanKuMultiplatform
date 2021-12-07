package com.hym.zhankukotlin.ui.main

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.GlideRequests
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.PreviewItemBinding
import com.hym.zhankukotlin.network.PreviewItem
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.ui.ThemeColorRetriever
import com.hym.zhankukotlin.ui.detail.DetailActivity
import com.hym.zhankukotlin.util.ViewUtils.getActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class PagingPreviewItemAdapter :
    PagingDataAdapter<PreviewItem, BindingViewHolder<PreviewItemBinding>>(PreviewItemCallback) {
    object PreviewItemCallback : DiffUtil.ItemCallback<PreviewItem>() {
        override fun areItemsTheSame(oldItem: PreviewItem, newItem: PreviewItem): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PreviewItem, newItem: PreviewItem): Boolean {
            return true
        }
    }

    companion object {
        @JvmStatic
        val previewRecyclerPool = RecyclerView.RecycledViewPool()
        const val BUTTON_ITEM_TYPE = 1
        val TAG = PagingPreviewItemAdapter::class.simpleName

        init {
            previewRecyclerPool.setMaxRecycledViews(BUTTON_ITEM_TYPE, 20)
        }
    }

    private var mRequestManager: GlideRequests? = null

    override fun getItemViewType(position: Int): Int {
        return BUTTON_ITEM_TYPE
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<PreviewItemBinding> {
        val binding: PreviewItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(MyApplication.INSTANCE), R.layout.preview_item, parent, false
        )
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<PreviewItemBinding>, position: Int
    ) {
        val previewItem = getItem(position) // Note that item may be null.
        val imageUrl = previewItem?.imageUrl
        if (imageUrl == null) {
            Log.e(TAG, "previewItem or previewItem.imageUrl of position[$position] is null!")
            return
        }
        val binding = holder.binding
        binding.previewItem = previewItem
        binding.previewImg.setOnClickListener { v ->
            val activity = v.getActivity() ?: return@setOnClickListener
            val intent = Intent(activity, DetailActivity::class.java)
                .putExtra(DetailActivity.KEY_TITLE, previewItem.title)
                .putExtra(DetailActivity.KEY_URL, previewItem.targetUrl)
            val bitmap = (binding.previewImg.drawable as? BitmapDrawable)?.bitmap
            if (bitmap != null && activity is LifecycleOwner) {
                activity.lifecycleScope.launch {
                    val mainThemeColor = withTimeoutOrNull(200) {
                        return@withTimeoutOrNull ThemeColorRetriever.getMainThemeColor(bitmap)
                    }
                    intent.putExtra(DetailActivity.KEY_COLOR, mainThemeColor)
                    activity.startActivity(intent)
                }
            } else {
                activity.startActivity(intent)
            }
        }
        mRequestManager?.run {
            load(imageUrl)
                .into(binding.previewImg)
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<PreviewItemBinding>) {
        holder.binding.previewImg.setOnClickListener(null)
        mRequestManager?.clear(holder.binding.previewImg)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setRecycledViewPool(previewRecyclerPool)
        when (val layoutManager = recyclerView.layoutManager) {
            is LinearLayoutManager -> layoutManager.recycleChildrenOnDetach = true
            is FlexboxLayoutManager -> layoutManager.recycleChildrenOnDetach = true
        }
        mRequestManager = GlideApp.with(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setRecycledViewPool(null)
        mRequestManager = null
    }
}