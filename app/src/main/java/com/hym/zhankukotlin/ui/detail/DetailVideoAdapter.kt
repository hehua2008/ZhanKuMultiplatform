package com.hym.zhankukotlin.ui.detail

import android.graphics.Color
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.video.VideoSize
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.model.ProductVideo
import com.hym.zhankukotlin.player.CustomPlayerView
import com.hym.zhankukotlin.player.PlayerProvider

class DetailVideoAdapter(private val playerProvider: PlayerProvider) :
    ListAdapter<ProductVideo, DetailVideoAdapter.ViewHolder>(ITEM_CALLBACK) {
    companion object {
        val ITEM_CALLBACK = object : DiffUtil.ItemCallback<ProductVideo>() {
            override fun areItemsTheSame(oldItem: ProductVideo, newItem: ProductVideo): Boolean {
                return oldItem.url == newItem.url
            }

            override fun areContentsTheSame(oldItem: ProductVideo, newItem: ProductVideo): Boolean {
                return oldItem == newItem
            }
        }

        private const val TAG = "DetailVideoAdapter"
        private const val PREPARE_DELAY = 300L
        private const val WHAT_PREPARE = 2022
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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
        return ViewHolder(constraintLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val productVideo = getItem(position)
        holder.bindData(productVideo)
    }

    override fun onViewRecycled(holder: ViewHolder) {
        holder.unbindData()
    }

    override fun onViewAttachedToWindow(holder: ViewHolder) {
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

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.playerView.player?.pause()
        mHandler.removeMessages(WHAT_PREPARE, holder.playerView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        //recyclerView.addOnScrollListener(mOnScrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        //recyclerView.removeOnScrollListener(mOnScrollListener)
    }

    inner class ViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup),
        Player.Listener {
        val playerView: CustomPlayerView = viewGroup.findViewById(R.id.player_view)

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
                        addListener(this@ViewHolder)
                        setMediaItem(mediaItem)
                    }
                }
            }
        }

        fun unbindData() {
            playerView.player?.let {
                it.removeListener(this)
                // Recycle player
                playerProvider.recycle(it)
                playerView.player = null
            }
        }

        override fun onVideoSizeChanged(videoSize: VideoSize) {
            if (videoSize.width == 0 || videoSize.height == 0) return
            playerView.run {
                layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                    dimensionRatio = "${videoSize.width}:${videoSize.height}"
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
            (recyclerView.findViewHolderForLayoutPosition(playPos) as? ViewHolder)?.run {
                playerView.player?.let {
                    playerProvider.pauseOtherActivePlayers(it)
                    it.play()
                }
            }
            mLastStateIsIdle = true
        }
    }
}