package com.hym.zhankukotlin.ui.main

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexboxLayoutManager
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.GlideRequests
import com.hym.zhankukotlin.databinding.PreviewItemLayoutBinding
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.ui.ThemeColorRetriever
import com.hym.zhankukotlin.ui.author.AuthorItemFragment
import com.hym.zhankukotlin.ui.detail.DetailActivity
import com.hym.zhankukotlin.ui.tag.TagActivity
import com.hym.zhankukotlin.util.ViewUtils.getActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class PagingPreviewItemAdapter :
    PagingDataAdapter<Content, BindingViewHolder<PreviewItemLayoutBinding>>(PreviewItemCallback) {
    object PreviewItemCallback : DiffUtil.ItemCallback<Content>() {
        override fun areItemsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Content, newItem: Content): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val TAG = "PagingPreviewItemAdapter"

        const val PREVIEW_ITEM_TYPE = 1
    }

    private var mRequestManager: GlideRequests? = null

    override fun getItemViewType(position: Int): Int {
        return PREVIEW_ITEM_TYPE
    }

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<PreviewItemLayoutBinding> {
        val binding = PreviewItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return BindingViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: BindingViewHolder<PreviewItemLayoutBinding>, position: Int
    ) {
        val previewItem = getItem(position) // Note that item may be null.
        val imageUrl = previewItem?.cover1x
        if (imageUrl == null) {
            Log.e(TAG, "previewItem or previewItem.imageUrl of position[$position] is null!")
            return
        }
        val binding = holder.binding
        binding.root.setContent(previewItem)
        binding.previewImg.setOnClickListener { v ->
            val activity = v.getActivity() ?: return@setOnClickListener
            val intent = Intent(activity, DetailActivity::class.java)
                .putExtra(DetailActivity.KEY_TITLE, previewItem.formatTitle)
                .putExtra(DetailActivity.KEY_CONTENT_TYPE, previewItem.objectType)
                .putExtra(DetailActivity.KEY_CONTENT_ID, previewItem.contentId)
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
        View.OnClickListener { v ->
            val activity = v.getActivity() ?: return@OnClickListener
            val intent = Intent(activity, TagActivity::class.java)
            previewItem.creatorObj.run {
                intent.putExtra(AuthorItemFragment.AUTHOR_UID, id)
                intent.putExtra(AuthorItemFragment.AUTHOR_NAME, username)
            }
            activity.startActivity(intent)
        }.let {
            binding.avatar.setOnClickListener(it)
            binding.author.setOnClickListener(it)
        }
        mRequestManager?.run {
            load(imageUrl)
                .into(binding.previewImg)

            load(previewItem.creatorObj.avatar1x)
                .into(binding.avatar)
        }
    }

    override fun onViewRecycled(holder: BindingViewHolder<PreviewItemLayoutBinding>) {
        holder.binding.previewImg.setOnClickListener(null)
        mRequestManager?.run {
            clear(holder.binding.previewImg)
            clear(holder.binding.avatar)
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        /*
        when (val layoutManager = recyclerView.layoutManager) {
            is LinearLayoutManager -> layoutManager.recycleChildrenOnDetach = true
            is FlexboxLayoutManager -> layoutManager.recycleChildrenOnDetach = true
        }
        */
        mRequestManager = GlideApp.with(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = null
    }
}