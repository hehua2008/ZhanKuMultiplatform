package com.hym.photoviewer

import android.app.Activity
import android.graphics.Bitmap
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.hym.photoviewer.databinding.FragmentPhotoViewBinding
import kotlin.math.absoluteValue

/**
 * Displays a photo.
 * Public no-arg constructor for allowing the framework to handle orientation changes
 */
class PhotoViewFragment : Fragment(), OnScreenListener, RequestListener<Bitmap> {
    companion object {
        const val ARG_POSITION = "position"
        const val ARG_PHOTO_INFO = "photo_info"
        const val ARG_WATCH_NETWORK = "watch_network"

        private const val TAG = "PhotoViewFragment"

        /**
         * The animation time of fade in is 500ms, so it should be grater than 500ms.
         */
        private const val PROGRESS_SHOW_DELAY = 600L

        /**
         * Create a [PhotoViewFragment].
         */
        fun newInstance(arguments: Bundle): PhotoViewFragment {
            val fragment = PhotoViewFragment()
            fragment.arguments = arguments
            return fragment
        }
    }

    /**
     * The position in [PhotoViewPager]
     */
    private var mPosition = 0

    private lateinit var mCallback: PhotoViewCallback
    private lateinit var mConn: ConnectivityManager

    private var mBinding: FragmentPhotoViewBinding? = null
    private val binding get() = checkNotNull(mBinding)

    private var mPhotoProgressBar: ProgressBarWrapper? = null
    private var mPhotoViewTarget: PhotoViewTarget? = null

    private lateinit var mRequestManager: RequestManager

    private val mNetworkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            if (!mConnected) {
                mConnected = true
                mBinding?.root?.post(this@PhotoViewFragment::loadPhoto)
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
     * True if the PhotoViewFragment should watch the network state in order to restart loaders.
     */
    private var mWatchNetworkState = false

    /**
     * Whether or not there is currently a connection to the internet
     */
    private var mConnected = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = (savedInstanceState ?: arguments)!!
        mPosition = bundle.getInt(ARG_POSITION)
        mPhotoInfo = bundle.getParcelable(ARG_PHOTO_INFO)!!
        mWatchNetworkState = bundle.getBoolean(ARG_WATCH_NETWORK, false)
        mRequestManager = Glide.with(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentPhotoViewBinding.inflate(inflater, container, false).apply {
            photoView.contentDescription = mPhotoInfo.description
            photoView.setOnClickListener {
                mCallback.toggleFullScreen()
            }
            registerForContextMenu(photoView)
            swipeRefresh.setOnRefreshListener {
                loadPhoto()
            }
            mPhotoProgressBar = ProgressBarWrapper(determinateProgress, indeterminateProgress, true)
            mPhotoViewTarget = PhotoViewTarget(photoView)
        }
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val activity: Activity = requireActivity()
        require(activity is PhotoViewCallback) { "Activity must be a implementation class of PhotoViewCallback" }
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
        binding.root.removeCallbacks(mShowProgressRunnable)
        unregisterForContextMenu(binding.photoView)
        binding.photoView.clear()
        mRequestManager.clear(mPhotoViewTarget)
        mBinding = null
        mPhotoProgressBar = null
        mPhotoViewTarget = null
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
                mRequestManager.clear(mPhotoViewTarget)
            }
        }
    }

    /**
     * Reset the views to their default states
     */
    private fun resetViews() {
        binding.photoView.resetTransformations()
    }

    override fun onInterceptMoveLeft(origX: Float, origY: Float): Boolean {
        return if (!isActivated()) false
        else binding.photoView.interceptMoveLeft(origX, origY)
    }

    override fun onInterceptMoveRight(origX: Float, origY: Float): Boolean {
        return if (!isActivated()) false
        else binding.photoView.interceptMoveRight(origX, origY)
    }

    override fun onLoadFailed(
        e: GlideException?,
        model: Any?,
        target: Target<Bitmap>?,
        isFirstResource: Boolean
    ): Boolean {
        mBinding?.run {
            root.removeCallbacks(mShowProgressRunnable)
            swipeRefresh.isEnabled = true
            swipeRefresh.isRefreshing = false
            retryText.isVisible = true
        }
        mPhotoProgressBar?.setVisibility(View.GONE)
        return false
    }

    override fun onResourceReady(
        resource: Bitmap,
        model: Any?,
        target: Target<Bitmap>?,
        dataSource: DataSource?,
        isFirstResource: Boolean
    ): Boolean {
        mBinding?.run {
            root.removeCallbacks(mShowProgressRunnable)
            swipeRefresh.isEnabled = false
            swipeRefresh.isRefreshing = false
            retryText.isVisible = false
        }
        mPhotoProgressBar?.setVisibility(View.GONE)
        return false
    }

    /**
     * Returns `true` if a photo is loading. Otherwise, returns `false`.
     */
    private fun isPhotoLoading(): Boolean = mPhotoViewTarget?.request?.isRunning ?: false

    /**
     * Returns `true` if a photo has been loaded. Otherwise, returns `false`.
     */
    private fun isPhotoLoaded(): Boolean = mPhotoViewTarget?.request?.isComplete ?: false

    private fun isActivated(): Boolean = mCallback.getCurrentPosition() == mPosition

    private fun loadPhoto() {
        val binding = mBinding ?: return
        val photoViewTarget = mPhotoViewTarget ?: return
        if ((mCallback.getCurrentPosition() - mPosition).absoluteValue > 1) return
        if (isPhotoLoading() || isPhotoLoaded()) return
        binding.run {
            root.postDelayed(mShowProgressRunnable, PROGRESS_SHOW_DELAY)
            retryText.isVisible = false
        }
        mRequestManager
            .asBitmap()
            .thumbnail(
                if (mPhotoInfo.thumb == mPhotoInfo.original) null
                else mRequestManager.asBitmap().load(mPhotoInfo.thumb).addListener(this)
            )
            .load(mPhotoInfo.original)
            .override(Target.SIZE_ORIGINAL)
            .priority(if (isActivated()) Priority.IMMEDIATE else Priority.LOW)
            .addListener(this)
            .into(photoViewTarget)
    }
}