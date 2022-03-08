package com.hym.zhankukotlin.ui.main

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
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.GlideAppExtension
import com.hym.zhankukotlin.GlideRequests
import com.hym.zhankukotlin.databinding.PreviewItemLayoutBinding
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.ui.ThemeColorRetriever
import com.hym.zhankukotlin.ui.author.AuthorItemFragment
import com.hym.zhankukotlin.ui.detail.DetailActivity
import com.hym.zhankukotlin.ui.tag.TagActivity
import com.hym.zhankukotlin.util.getActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.math.abs

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

    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        var mScrolledY = 0F
        var mLastScrollState = RecyclerView.SCROLL_STATE_IDLE
        val mVelocityTracker: VelocityTracker = VelocityTracker.obtain()

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            mScrolledY += dy
        }

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                mScrolledY = 0F
                mVelocityTracker.clear()
                mRequestManager?.let {
                    if (it.isPaused) {
                        //Log.w(TAG, "resumeRequestsRecursive")
                        it.resumeRequestsRecursive()
                    }
                }
            } else if (mLastScrollState == RecyclerView.SCROLL_STATE_IDLE) {
                Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
                    override fun doFrame(frameTimeNanos: Long) {
                        if (mLastScrollState == RecyclerView.SCROLL_STATE_IDLE) return

                        val now = frameTimeNanos / 1000000
                        val ev =
                            MotionEvent.obtain(now, now, MotionEvent.ACTION_MOVE, 0F, mScrolledY, 0)
                        mVelocityTracker.addMovement(ev)
                        ev.recycle()

                        mVelocityTracker.computeCurrentVelocity(
                            1000,
                            recyclerView.maxFlingVelocity.toFloat()
                        )
                        val velocityY = mVelocityTracker.yVelocity
                        //Log.w(TAG, "mVelocityTracker.yVelocity=$velocityY")
                        mRequestManager?.let {
                            val isPaused = it.isPaused
                            if (!isPaused && abs(velocityY) >= recyclerView.resources.displayMetrics.density * 6000) {
                                //Log.w(TAG, "pauseRequestsRecursive velocityY=$velocityY")
                                it.pauseRequestsRecursive()
                            } else if (isPaused && abs(velocityY) < recyclerView.resources.displayMetrics.density * 2000) {
                                //Log.w(TAG, "resumeRequestsRecursive velocityY=$velocityY")
                                it.resumeRequestsRecursive()
                            }
                        }

                        Choreographer.getInstance().postFrameCallback(this)
                    }
                })
            }
            mLastScrollState = newState
        }
    }

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
        recyclerView.addOnScrollListener(mOnScrollListener)
        mRequestManager = GlideApp.with(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.removeOnScrollListener(mOnScrollListener)
        mRequestManager = null
    }
}