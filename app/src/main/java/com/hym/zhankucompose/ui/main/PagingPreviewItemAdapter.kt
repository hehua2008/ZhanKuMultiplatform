package com.hym.zhankucompose.ui.main

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.view.*
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hym.zhankucompose.GlideAppExtension
import com.hym.zhankucompose.databinding.PreviewItemLayoutBinding
import com.hym.zhankucompose.model.Content
import com.hym.zhankucompose.ui.BindingViewHolder
import com.hym.zhankucompose.ui.FastScrollListener
import com.hym.zhankucompose.ui.ThemeColorRetriever
import com.hym.zhankucompose.ui.author.AuthorItemFragment
import com.hym.zhankucompose.ui.detail.DetailActivity
import com.hym.zhankucompose.ui.tag.TagActivity
import com.hym.zhankucompose.util.getActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

class PagingPreviewItemAdapter :
    PagingDataAdapter<Content, BindingViewHolder<PreviewItemLayoutBinding>>(PreviewItemCallback),
    FastScrollListener.FastScrollCallback {
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

    private var mRequestManager: RequestManager? = null

    private val mFastScrollListener = FastScrollListener(this)

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
            val bitmap = binding.previewImg.drawable?.let {
                when (it) {
                    is BitmapDrawable -> return@let it.bitmap
                    is LayerDrawable -> {
                        return@let (it.numberOfLayers - 1 downTo 0).firstNotNullOfOrNull { idx ->
                            (it.getDrawable(idx) as? BitmapDrawable)?.bitmap
                        }
                    }
                    else -> return@let null
                }
            }
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
            val context = v.context
            val intent = Intent(context, TagActivity::class.java)
                .putExtra(AuthorItemFragment.AUTHOR, previewItem.creatorObj)
            context.startActivity(intent)
        }.let {
            binding.avatar.setOnClickListener(it)
            binding.author.setOnClickListener(it)
        }
        mRequestManager?.run {
            load(imageUrl)
                .transition(GlideAppExtension.DRAWABLE_CROSS_FADE)
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
        mFastScrollListener.attachToRecyclerView(recyclerView)
        mRequestManager = Glide.with(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mFastScrollListener.detachFromRecyclerView()
        mRequestManager = null
    }

    override fun onStartFastScroll() {
        mRequestManager?.run {
            if (!isPaused) {
                Log.w(TAG, "pauseRequestsRecursive")
                pauseRequestsRecursive()
            }
        }
    }

    override fun onStopFastScroll() {
        mRequestManager?.run {
            if (isPaused) {
                Log.w(TAG, "resumeRequestsRecursive")
                resumeRequestsRecursive()
            }
        }
    }
}