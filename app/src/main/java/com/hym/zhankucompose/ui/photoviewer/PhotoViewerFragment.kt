package com.hym.zhankucompose.ui.photoviewer

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.PointF
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntDef
import androidx.annotation.MainThread
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.request.FutureTarget
import com.davemorrissey.labs.subscaleview.ImageSource
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView
import com.davemorrissey.labs.subscaleview.UriProvider
import com.hym.zhankucompose.databinding.FragmentPhotoViewerBinding
import com.hym.zhankucompose.photo.PhotoInfo
import com.hym.zhankucompose.ui.ProgressBarWrapper
import java.io.File
import java.util.concurrent.CancellationException
import java.util.concurrent.Semaphore
import kotlin.math.absoluteValue

/**
 * Displays a photo.
 * Public no-arg constructor for allowing the framework to handle orientation changes
 */
class PhotoViewerFragment : Fragment(), OnScreenListener,
    SubsamplingScaleImageView.OnImageEventListener {
    companion object {
        const val ARG_POSITION = "position"
        const val ARG_PHOTO_INFO = "photo_info"
        const val ARG_WATCH_NETWORK = "watch_network"

        private const val STATE_INIT = 0
        private const val STATE_LOADING = 1
        private const val STATE_PREVIEW_LOADED = 2
        private const val STATE_LOADED = 3
        private const val STATE_ERROR = -1

        @IntDef(STATE_INIT, STATE_LOADING, STATE_PREVIEW_LOADED, STATE_LOADED, STATE_ERROR)
        @Retention(AnnotationRetention.BINARY)
        annotation class LoadState

        private const val TAG = "PhotoViewerFragment"

        /**
         * The animation time of fade in is 500ms, so it should be grater than 500ms.
         */
        private const val PROGRESS_SHOW_DELAY = 600L

        /**
         * Create a [PhotoViewerFragment].
         */
        fun newInstance(arguments: Bundle): PhotoViewerFragment {
            val fragment = PhotoViewerFragment()
            fragment.arguments = arguments
            return fragment
        }

        private fun PhotoInfo<*>.canShowThumb(): Boolean {
            return hasThumb() && width > 0 && height > 0
        }
    }

    /**
     * The position in [ViewPager]
     */
    private var mPosition = 0

    private lateinit var mCallback: PhotoViewerCallback
    private lateinit var mConn: ConnectivityManager

    private var mBinding: FragmentPhotoViewerBinding? = null
    private var mPhotoProgressBar: ProgressBarWrapper? = null

    private lateinit var mRequestManager: RequestManager
    private var mThumbUriProvider: ModelUriProvider? = null
    private lateinit var mOriginalUriProvider: ModelUriProvider

    @LoadState
    private var mLoadState = STATE_INIT

    private val mNetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            if (!mConnected) {
                mConnected = true
                mBinding?.root?.post(this@PhotoViewerFragment::loadPhoto)
            }
        }

        override fun onLost(network: Network) {
            mConnected = false
        }
    }

    private val mShowProgressRunnable = Runnable {
        mPhotoProgressBar?.setVisibility(View.VISIBLE)
    }

    /**
     * The info of a photo to display
     */
    private lateinit var mPhotoInfo: PhotoInfo<*>

    /**
     * True if the PhotoViewerFragment should watch the network state in order to restart loaders.
     */
    private var mWatchNetworkState = false

    /**
     * Whether or not there is currently a connection to the internet
     */
    private var mConnected = true

    private val mLastTouchPoint = PointF()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = (savedInstanceState ?: arguments)!!
        mPosition = bundle.getInt(ARG_POSITION)
        mPhotoInfo = bundle.getParcelable(ARG_PHOTO_INFO)!!
        mWatchNetworkState = bundle.getBoolean(ARG_WATCH_NETWORK, false)
        mRequestManager = Glide.with(this)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        FragmentPhotoViewerBinding.inflate(inflater, container, false).run {
            mBinding = this
            photoView.contentDescription = mPhotoInfo.description
            photoView.setOnClickListener {
                mCallback.toggleFullScreen()
            }
            photoView.setOnTouchListener { _, event ->
                mLastTouchPoint.set(event.x, event.y)
                false
            }
            photoView.setOnLongClickListener {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) it.showContextMenu()
                else it.showContextMenu(mLastTouchPoint.x, mLastTouchPoint.y)
            }
            photoView.setOnImageEventListener(this@PhotoViewerFragment)
            registerForContextMenu(photoView)

            mOriginalUriProvider = ModelUriProvider(mPhotoInfo.original)
            if (mPhotoInfo.canShowThumb()) {
                mThumbUriProvider = ModelUriProvider(mPhotoInfo.thumb)
                    .also {
                        photoView.setImage(
                            ImageSource.uriProvider(mOriginalUriProvider)
                                .dimensions(mPhotoInfo.width, mPhotoInfo.height),
                            ImageSource.uriProvider(it)
                        )
                    }
            } else {
                photoView.setImage(ImageSource.uriProvider(mOriginalUriProvider))
            }

            swipeRefresh.setOnRefreshListener {
                loadPhoto()
            }

            mPhotoProgressBar = ProgressBarWrapper(determinateProgress, indeterminateProgress, true)

            return root
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity: Activity = requireActivity()
        require(activity is PhotoViewerCallback) { "Activity must be a implementation class of PhotoViewCallback" }
        mCallback = activity
        mCallback.addScreenListener(mPosition, this)
        mConn = ContextCompat.getSystemService(activity, ConnectivityManager::class.java)!!
    }

    override fun onStart() {
        super.onStart()
        loadPhoto()
    }

    override fun onResume() {
        super.onResume()
        if (mWatchNetworkState) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                mConn.registerDefaultNetworkCallback(mNetworkCallback)
            } else {
                mConn.registerNetworkCallback(NetworkRequest.Builder().build(), mNetworkCallback)
            }
        }
        loadPhoto()
    }

    override fun onPause() {
        // Remove listeners
        if (mWatchNetworkState) {
            mConn.unregisterNetworkCallback(mNetworkCallback)
        }
        super.onPause()
    }

    override fun onDestroyView() {
        mCallback.removeScreenListener(mPosition)
        // Clean up views and other components
        mBinding?.run {
            root.removeCallbacks(mShowProgressRunnable)
            unregisterForContextMenu(photoView)
            photoView.recycle()
        }
        mThumbUriProvider?.cancel()
        mOriginalUriProvider.cancel()
        mBinding = null
        mPhotoProgressBar = null
        mThumbUriProvider = null
        mLoadState = STATE_INIT
        super.onDestroyView()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.run {
            putInt(ARG_POSITION, mPosition)
            putParcelable(ARG_PHOTO_INFO, mPhotoInfo as Parcelable)
            putBoolean(ARG_WATCH_NETWORK, mWatchNetworkState)
        }
    }

    override fun onFullScreenChanged(fullScreen: Boolean) {
    }

    override fun onFragmentActivated() {
        when ((mCallback.getCurrentPosition() - mPosition).absoluteValue) {
            0 -> {
                loadPhoto()
            }
            1 -> {
                resetViews()
                loadPhoto()
            }
            else -> {
                resetViews()
            }
        }
    }

    /**
     * Reset the views to their default states
     */
    private fun resetViews() {
        mBinding?.photoView?.run {
            if (mLoadState != STATE_PREVIEW_LOADED && mLoadState != STATE_LOADED) return
            animateScaleAndCenter(minScale, center)?.start() ?: resetScaleAndCenter()
        }
    }

    private fun loadPhoto() {
        if (mLoadState == STATE_LOADED) return
        val isThumb = when ((mCallback.getCurrentPosition() - mPosition).absoluteValue) {
            0 -> false
            1 -> mPhotoInfo.canShowThumb()
            else -> return
        }
        mThumbUriProvider?.startLoad()
        if (isThumb) return
        showLoading()
        mOriginalUriProvider.startLoad()
    }

    @MainThread
    private fun showLoading() {
        mBinding?.run {
            root.postDelayed(mShowProgressRunnable, PROGRESS_SHOW_DELAY)
            retryText.isVisible = false
        }
    }

    @MainThread
    private fun hideLoading(hasError: Boolean = false) {
        mBinding?.run {
            root.removeCallbacks(mShowProgressRunnable)
            swipeRefresh.isEnabled = hasError
            swipeRefresh.isRefreshing = false
            retryText.isVisible = hasError
        }
        mPhotoProgressBar?.setVisibility(View.GONE)
    }

    override fun onReady() {
    }

    override fun onPreviewLoaded() {
        mLoadState = STATE_PREVIEW_LOADED
        if ((mCallback.getCurrentPosition() - mPosition).absoluteValue <= 1) {
            mOriginalUriProvider.startPreload()
        }
    }

    override fun onImageLoaded() {
        mLoadState = STATE_LOADED
        hideLoading()
    }

    override fun onPreviewLoadError(e: Exception) {
    }

    override fun onImageLoadError(e: Exception) {
        mLoadState = STATE_ERROR
        hideLoading(true)
    }

    override fun onTileLoadError(e: Exception) {
        mLoadState = STATE_ERROR
        hideLoading(true)
    }

    override fun onPreviewReleased() {
    }

    inner class ModelUriProvider(val model: Any) : UriProvider() {
        private val semaphore = Semaphore(0) // Only works on first load

        @Volatile
        private var thread: Thread? = null

        @Volatile
        private var futureTarget: FutureTarget<File>? = null

        @Synchronized
        @Throws(CancellationException::class, Exception::class)
        override fun provide(context: Context): Uri {
            try {
                futureTarget?.let {
                    return it.get().toUri()
                }

                thread = Thread.currentThread()

                semaphore.acquire() // start preload
                futureTarget = mRequestManager.download(model)
                    .priority(if (mCallback.getCurrentPosition() == mPosition) Priority.IMMEDIATE else Priority.LOW)
                    .submit()

                semaphore.acquire() // start load
                val uri = futureTarget!!.get().toUri()

                thread = null

                return uri
            } catch (e: InterruptedException) {
                throw CancellationException("Cancel load $model")
            }
        }

        fun startPreload() {
            semaphore.release()
        }

        fun startLoad() {
            semaphore.release(2)
        }

        fun cancel() {
            futureTarget?.cancel(true)
            thread?.interrupt()
        }
    }
}