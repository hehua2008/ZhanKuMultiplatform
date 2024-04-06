package com.hym.zhankucompose.ui.detail

import android.annotation.SuppressLint
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
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.HtmlCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.ParserException
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.hym.zhankucompose.GlideAppExtension
import com.hym.zhankucompose.R
import com.hym.zhankucompose.model.ProductVideo
import com.hym.zhankucompose.photo.UrlPhotoInfo
import com.hym.zhankucompose.player.CustomPlayerView
import com.hym.zhankucompose.player.PlayerProvider
import com.hym.zhankucompose.ui.ImageViewHeightListener
import com.hym.zhankucompose.ui.RatioImageView
import com.hym.zhankucompose.ui.webview.WebViewActivity
import com.hym.zhankucompose.util.getActivity

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

            @SuppressLint("DiffUtilEquals")
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

    private var mRequestManager: RequestManager? = null

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == DetailContent.CONTENT_IMAGE) {
            /**
             * <com.hym.zhankucompose.ui.RatioImageView
             *     android:layout_width="match_parent"
             *     android:layout_height="0dp"
             *     android:adjustViewBounds="true"
             *     android:contentDescription="Loading..."
             *     android:scaleType="fitCenter"
             *     app:widthHeightRatio="3:2">
             * </com.hym.zhankucompose.ui.RatioImageView>
             */
            val context = parent.context
            val imageView = RatioImageView(context).apply {
                adjustViewBounds = true
                contentDescription = "Loading..."
                scaleType = ImageView.ScaleType.FIT_CENTER
                widthHeightRatio = "3:2"
            }
            imageView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0
            )
            return ImageViewHolder(imageView)
        } else if (viewType == DetailContent.CONTENT_VIDEO) {
            /**
             * <com.hym.zhankucompose.player.CustomPlayerView
             *     android:id="@+id/player_view"
             *     android:layout_width="match_parent"
             *     android:layout_height="0dp"
             *     android:contentDescription="Loading..."
             *     app:show_next_button="false"
             *     app:show_previous_button="false"
             *     app:show_fastforward_button="false"
             *     app:show_rewind_button="false"
             *     app:show_timeout="2000"
             *     app:shutter_background_color="@android:color/white"
             *     app:widthHeightRatio="16:9">
             * </com.hym.zhankucompose.player.CustomPlayerView>
             */
            val context = parent.context
            val playerView = CustomPlayerView(context).apply {
                contentDescription = "Loading..."
                setShowNextButton(false)
                setShowPreviousButton(false)
                setShowFastForwardButton(false)
                setShowRewindButton(false)
                controllerShowTimeoutMs = 2000
                setShutterBackgroundColor(Color.WHITE)
                widthHeightRatio = "16:9"
            }
            playerView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0
            )
            return VideoViewHolder(playerView)
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
                    widthHeightRatio = "${img.width}:${img.height}"
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
                    activity.launchZoomImagePagerActivity(
                        photoInfos, imageList.indexOf(detailImage)
                    )
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
        mRequestManager = Glide.with(recyclerView)
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

    class ImageViewHolder(val imageView: RatioImageView) : RecyclerView.ViewHolder(imageView)

    inner class VideoViewHolder(val playerView: CustomPlayerView) :
        RecyclerView.ViewHolder(playerView), Player.Listener {
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
            playerView.widthHeightRatio = "${videoSize.width}:${videoSize.height}"
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