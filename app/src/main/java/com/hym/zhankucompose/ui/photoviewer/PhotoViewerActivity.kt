package com.hym.zhankucompose.ui.photoviewer

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.viewpager.widget.ViewPager
import com.hym.zhankucompose.BaseActivity
import com.hym.zhankucompose.R
import com.hym.zhankucompose.databinding.ActivityPhotoViewerBinding
import com.hym.zhankucompose.photo.UrlPhotoInfo
import com.hym.zhankucompose.work.DownloadWorker

class PhotoViewerActivity : BaseActivity(), ViewPager.OnPageChangeListener,
    PhotoViewerCallback {
    companion object {
        const val PHOTO_INFOS = "PHOTO_INFOS"
        const val CURRENT_POSITION = "CURRENT_POSITION"
        const val SCREEN_LOCATION = "SCREEN_LOCATION"

        private const val TAG = "PhotoViewerActivity"
    }

    private val screenListenerMap = mutableMapOf<Int, OnScreenListener>()

    private lateinit var binding: ActivityPhotoViewerBinding
    private lateinit var pagerAdapter: PhotoViewerPagerAdapter

    private var isFullScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val photoInfos: List<UrlPhotoInfo> = intent.getParcelableArrayListExtra(PHOTO_INFOS)!!
        val currentPosition = intent.getIntExtra(CURRENT_POSITION, 0)

        binding = ActivityPhotoViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showSystemBars(true)
        window.decorView.setOnApplyWindowInsetsListener { v, insets ->
            val insetsCompat = WindowInsetsCompat.toWindowInsetsCompat(insets, v)
            val statusBarHeight = insetsCompat.getInsets(WindowInsetsCompat.Type.statusBars()).top
            if (statusBarHeight > 0) {
                binding.actionBar.run {
                    layoutParams = (layoutParams as ViewGroup.MarginLayoutParams).also {
                        it.topMargin = statusBarHeight
                    }
                }
            }
            insets
        }

        pagerAdapter = PhotoViewerPagerAdapter(supportFragmentManager, photoInfos)

        binding.actionBar.run {
            title = getString(R.string.photo_viewer_count, currentPosition + 1, photoInfos.size)
            setNavigationOnClickListener { finish() }
        }
        binding.photoViewerPager.run {
            adapter = pagerAdapter
            //pageMargin = resources.getDimensionPixelSize(R.dimen.photo_page_margin)
            offscreenPageLimit = 2
            addOnPageChangeListener(this@PhotoViewerActivity)
            currentItem = currentPosition
        }
    }

    override fun finish() {
        val screenLocation = binding.photoViewerPager.run {
            IntArray(2).let {
                getLocationOnScreen(it)
                Rect(it[0], it[1], it[0] + width, it[1] + height)
            }
        }
        val data = Intent()
            .putExtra(CURRENT_POSITION, getCurrentPosition())
            .putExtra(SCREEN_LOCATION, screenLocation)
        setResult(RESULT_OK, data)
        super.finish()
        overridePendingTransition(0, android.R.anim.fade_out)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        binding.actionBar.title =
            getString(R.string.photo_viewer_count, position + 1, pagerAdapter.count)
        screenListenerMap.values.forEach {
            it.onFragmentActivated()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun addScreenListener(position: Int, listener: OnScreenListener) {
        screenListenerMap[position] = listener
    }

    override fun removeScreenListener(position: Int) {
        screenListenerMap.remove(position)
    }

    override fun toggleFullScreen() {
        setFullScreen(!isFullScreen)
    }

    override fun getCurrentPosition(): Int {
        return binding.photoViewerPager.currentItem
    }

    private fun showSystemBars(show: Boolean) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowCompat.getInsetsController(window, window.decorView).run {
            systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            if (show) show(WindowInsetsCompat.Type.systemBars())
            else hide(WindowInsetsCompat.Type.systemBars())
        }
    }

    private fun setFullScreen(fullScreen: Boolean) {
        if (isFullScreen == fullScreen) return
        isFullScreen = fullScreen
        showSystemBars(!fullScreen)
        binding.actionBar.let {
            it.animate().run {
                alpha(if (fullScreen) 0F else 1F)
                interpolator = FastOutSlowInInterpolator()
                duration = 250
                withStartAction {
                    it.isVisible = true
                }
                withEndAction {
                    it.isVisible = !fullScreen
                    screenListenerMap.values.forEach { listener ->
                        listener.onFullScreenChanged(fullScreen)
                    }
                }
                start()
            }
        }
    }

    override fun onCreateContextMenu(
        menu: ContextMenu,
        v: View,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        menuInflater.inflate(R.menu.photo_viewer_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_current -> {
                DownloadWorker.enqueue(this, pagerAdapter.photoInfos[getCurrentPosition()].original)
                true
            }
            R.id.save_all -> {
                DownloadWorker.enqueue(this, pagerAdapter.photoInfos.map { it.original })
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}