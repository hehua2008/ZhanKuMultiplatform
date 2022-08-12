package com.hym.zhankukotlin.ui.detail

import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ParserException
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.GlideAppExtension
import com.hym.zhankukotlin.GlideRequests
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.model.ProductVideo
import com.hym.zhankukotlin.player.CustomPlayerView
import com.hym.zhankukotlin.player.PlayerProvider
import com.hym.zhankukotlin.ui.ImageViewHeightListener
import com.hym.zhankukotlin.ui.photoviewer.UrlPhotoInfo
import com.hym.zhankukotlin.ui.webview.WebViewActivity
import com.hym.zhankukotlin.util.getActivity

class DetailContentAdapter(private val playerProvider: PlayerProvider) :
    ListAdapter<DetailContent<*>, RecyclerView.ViewHolder>(ITEM_CALLBACK) {
    companion object {
        private const val TAG = "DetailContentAdapter"
        private const val PREPARE_DELAY = 300L
        private const val WHAT_PREPARE = 2022

        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<DetailContent<*>>() {
            override fun areItemsTheSame(
                oldItem: DetailContent<*>, newItem: DetailContent<*>
            ): Boolean {
                return oldItem.shallowEquals(newItem)
            }

            override fun areContentsTheSame(
                oldItem: DetailContent<*>, newItem: DetailContent<*>
            ): Boolean {
                return oldItem.data == newItem.data
            }
        }
    }

    private val mHandler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                WHAT_PREPARE -> {
                    val playerView = msg.obj as CustomPlayerView
                    playerView.player?.prepare()
                }
                else -> super.handleMessage(msg)
            }
        }
    }

    private var mFirstPlay = true

    private var mRequestManager: GlideRequests? = null

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == DetailContent.CONTENT_IMAGE) {
            /**
             * <androidx.constraintlayout.widget.ConstraintLayout
             *     android:layout_width="match_parent"
             *     android:layout_height="wrap_content">
             *
             *     <androidx.appcompat.widget.AppCompatImageView
             *         android:id="@+id/image_view"
             *         android:layout_width="match_parent"
             *         android:layout_height="0dp"
             *         android:adjustViewBounds="true"
             *         android:contentDescription="Loading..."
             *         android:scaleType="fitCenter"
             *         app:layout_constraintDimensionRatio="3:2" />
             * </androidx.constraintlayout.widget.ConstraintLayout>
             */
            val context = parent.context
            val constraintLayout = ConstraintLayout(context)
            constraintLayout.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val imageView = AppCompatImageView(context).apply {
                id = R.id.image_view
                adjustViewBounds = true
                contentDescription = "Loading..."
                scaleType = ImageView.ScaleType.FIT_CENTER
            }
            imageView.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0
            ).apply { dimensionRatio = "3:2" }
            constraintLayout.addView(imageView)
            return ImageViewHolder(constraintLayout)
        } else if (viewType == DetailContent.CONTENT_VIDEO) {
            /**
             * <androidx.constraintlayout.widget.ConstraintLayout
             *     android:layout_width="match_parent"
             *     android:layout_height="wrap_content">
             *
             *     <com.hym.zhankukotlin.player.CustomPlayerView
             *         android:id="@+id/player_view"
             *         android:layout_width="match_parent"
             *         android:layout_height="0dp"
             *         android:contentDescription="Loading..."
             *         app:show_next_button="false"
             *         app:show_previous_button="false"
             *         app:show_fastforward_button="false"
             *         app:show_rewind_button="false"
             *         app:show_timeout="2000"
             *         app:shutter_background_color="@android:color/white"
             *         app:layout_constraintDimensionRatio="16:9" />
             * </androidx.constraintlayout.widget.ConstraintLayout>
             */
            val context = parent.context
            val constraintLayout = ConstraintLayout(context)
            constraintLayout.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )
            val playerView = CustomPlayerView(context).apply {
                id = R.id.player_view
                contentDescription = "Loading..."
                setShowNextButton(false)
                setShowPreviousButton(false)
                setShowFastForwardButton(false)
                setShowRewindButton(false)
                controllerShowTimeoutMs = 2000
                setShutterBackgroundColor(Color.WHITE)
            }
            playerView.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0
            ).apply { dimensionRatio = "16:9" }
            constraintLayout.addView(playerView)
            return VideoViewHolder(constraintLayout)
        } else if (viewType == DetailContent.CONTENT_TEXT) {
            /**
             * <androidx.appcompat.widget.AppCompatTextView
             *     android:layout_width="match_parent"
             *     android:layout_height="wrap_content"
             *     android:textIsSelectable="true" />
             */
            val context = parent.context
            val textView = AppCompatTextView(context)
            textView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply {
                val horizontalMargin =
                    context.resources.getDimensionPixelSize(R.dimen.common_horizontal_margin)
                setMargins(horizontalMargin, 0, horizontalMargin, 0)
            }
            return TextViewHolder(textView)
        } else {
            throw IllegalArgumentException("viewType=$viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ImageViewHolder) {
            val img = (getItem(position) as DetailImage).data
            val hasSize = img.width != 0 && img.height != 0
            holder.imageView.run {
                if (hasSize) {
                    layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                        dimensionRatio = "${img.width}:${img.height}"
                    }
                }
                layout(0, 0, 0, 0)
                setOnClickListener { v ->
                    val activity = v.getActivity()
                    if (activity !is DetailActivity) return@setOnClickListener
                    val imageList = currentList.filterIsInstance<DetailImage>()
                    val photoInfos = imageList.map {
                        UrlPhotoInfo(
                            original = it.data.oriUrl,
                            thumb = it.data.url,
                            width = it.data.oriWidth,
                            height = it.data.oriHeight
                        )
                    }
                    // Get the item at this position every time since currentList may be changed.
                    val detailImage = getItem(position) as DetailImage
                    activity.launchPhotoViewerActivity(photoInfos, imageList.indexOf(detailImage))
                }
            }
            mRequestManager?.run {
                load(img.url)
                    //.transparentPlaceHolder()
                    .transition(GlideAppExtension.DRAWABLE_CROSS_FADE)
                    //.originalSize()
                    .run {
                        if (hasSize) this else addListener(ImageViewHeightListener)
                    }
                    .into(holder.imageView)
                    .waitForLayout()
            }
        } else if (holder is VideoViewHolder) {
            val productVideo = (getItem(position) as DetailVideo).data
            holder.bindData(productVideo)
        } else if (holder is TextViewHolder) {
            val text = (getItem(position) as DetailText).data
            holder.textView.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_COMPACT)
            holder.textView.fixTextSelection()
        } else {
            throw IllegalArgumentException("position($position): $holder")
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        if (holder is ImageViewHolder) {
            mRequestManager?.clear(holder.imageView)
        } else if (holder is VideoViewHolder) {
            holder.unbindData()
        } else if (holder is TextViewHolder) {
            holder.textView.text = ""
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        if (holder !is VideoViewHolder) return
        if (mFirstPlay) {
            holder.playerView.player?.run {
                prepare()
                play()
                mFirstPlay = false
                return
            }
        }
        val msg = Message.obtain(
            mHandler,
            WHAT_PREPARE,
            0,
            0,
            holder.playerView
        )
        mHandler.sendMessageDelayed(msg, PREPARE_DELAY)
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        if (holder !is VideoViewHolder) return
        holder.playerView.player?.pause()
        mHandler.removeMessages(WHAT_PREPARE, holder.playerView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = GlideApp.with(recyclerView)
        //recyclerView.addOnScrollListener(mOnScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = null
        //recyclerView.removeOnScrollListener(mOnScrollListener)
    }

    private fun TextView.fixTextSelection() {
        setTextIsSelectable(false)
        post { setTextIsSelectable(true) }
    }

    class TextViewHolder(val textView: AppCompatTextView) : RecyclerView.ViewHolder(textView)

    class ImageViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup) {
        val imageView: AppCompatImageView = viewGroup.findViewById(R.id.image_view)
    }

    inner class VideoViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup),
        Player.Listener {
        val playerView: CustomPlayerView = viewGroup.findViewById(R.id.player_view)
        var productVideo: ProductVideo? = null

        fun bindData(productVideo: ProductVideo?) {
            unbindData()
            when {
                productVideo == null -> return
                productVideo.url.isBlank() -> {
                    Log.w(TAG, "The url of productVideo(${productVideo.id}) is blank")
                }
                else -> {
                    val mediaItem = MediaItem.Builder()
                        .setUri(productVideo.url)
                        //.setMimeType(MimeTypes.APPLICATION_MP4)
                        .build()
                    playerView.player = playerProvider.obtain().apply {
                        addListener(this@VideoViewHolder)
                        setMediaItem(mediaItem)
                    }
                }
            }
            this.productVideo = productVideo
        }

        fun unbindData() {
            playerView.player?.let {
                it.removeListener(this)
                // Recycle player
                playerProvider.recycle(it)
                playerView.player = null
            }
            productVideo = null
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            if (videoSize.width == 0 || videoSize.height == 0) return
            playerView.run {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    dimensionRatio = "${videoSize.width}:${videoSize.height}"
                }
            }
        }

        override fun onPlayerError(error: PlaybackException) {
            if (error.cause is ParserException) {
                val productVideo = productVideo ?: return
                playerView.setOnClickListener {
                    val intent = Intent(it.context, WebViewActivity::class.java)
                        .putExtra(WebViewActivity.WEB_URL, productVideo.url)
                        .putExtra(WebViewActivity.WEB_TITLE, productVideo.name)
                    it.context.startActivity(intent)
                }
            }
        }
    }

    private val mOnScrollListener = object : RecyclerView.OnScrollListener() {
        private var mLastStateIsIdle = true

        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            if (newState != RecyclerView.SCROLL_STATE_IDLE) {
                if (mLastStateIsIdle) {
                    playerProvider.pauseOtherActivePlayers(null)
                    mLastStateIsIdle = false
                }
                return
            }
            val linearLayoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstPos = linearLayoutManager.findFirstVisibleItemPosition()
            val lastPos = linearLayoutManager.findLastVisibleItemPosition()
            val halfRecyclerViewVisibleRect = Rect().also {
                recyclerView.getGlobalVisibleRect(it)
                it.top = (it.top + it.bottom) / 2
            }
            var playPos = (firstPos + lastPos) / 2
            for (pos in firstPos..lastPos) {
                val view = linearLayoutManager.findViewByPosition(pos) ?: continue
                val viewVisibleRect = Rect().also {
                    view.getGlobalVisibleRect(it)
                }
                if (viewVisibleRect.intersect(halfRecyclerViewVisibleRect)) {
                    playPos = pos
                    break
                }
            }
            (recyclerView.findViewHolderForLayoutPosition(playPos) as? VideoViewHolder)?.run {
                playerView.player?.let {
                    playerProvider.pauseOtherActivePlayers(it)
                    it.play()
                }
            }
            mLastStateIsIdle = true
        }
    }
}