package com.hym.photoviewer

import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isVisible
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.viewpager.widget.ViewPager
import com.hym.photoviewer.databinding.ActivityPhotoViewBinding

class PhotoViewActivity : AppCompatActivity(), ViewPager.OnPageChangeListener,
    PhotoViewPager.OnInterceptTouchListener, PhotoViewCallback {
    companion object {
        const val PHOTO_INFOS = "PHOTO_INFOS"
        const val CURRENT_POSITION = "CURRENT_POSITION"
        const val SCREEN_LOCATION = "SCREEN_LOCATION"

        private const val TAG = "PhotoViewActivity"
    }

    private val screenListenerMap = mutableMapOf<Int, OnScreenListener>()

    private lateinit var binding: ActivityPhotoViewBinding
    private lateinit var pagerAdapter: PhotoViewPagerAdapter

    private var isFullScreen: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val photoInfos: List<UrlPhotoInfo> = intent.getParcelableArrayListExtra(PHOTO_INFOS)!!
        val currentPosition = intent.getIntExtra(CURRENT_POSITION, 0)

        binding = ActivityPhotoViewBinding.inflate(layoutInflater)
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

        pagerAdapter = PhotoViewPagerAdapter(supportFragmentManager, photoInfos)

        binding.actionBar.run {
            title = getString(R.string.photo_view_count, currentPosition + 1, photoInfos.size)
            setNavigationOnClickListener { finish() }
        }
        binding.photoViewPager.run {
            adapter = pagerAdapter
            //pageMargin = resources.getDimensionPixelSize(R.dimen.photo_page_margin)
            offscreenPageLimit = 2
            addOnPageChangeListener(this@PhotoViewActivity)
            setOnInterceptTouchListener(this@PhotoViewActivity)
            currentItem = currentPosition
        }
    }

    override fun finish() {
        val screenLocation = binding.photoViewPager.run {
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
            getString(R.string.photo_view_count, position + 1, pagerAdapter.count)
        screenListenerMap.values.forEach {
            it.onFragmentActivated()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onTouchIntercept(origX: Float, origY: Float): PhotoViewPager.InterceptType {
        val screenListeners = screenListenerMap.values
        val interceptLeft = screenListeners.any {
            it.onInterceptMoveLeft(origX, origY)
        }
        val interceptRight = screenListeners.any {
            it.onInterceptMoveRight(origX, origY)
        }
        return if (interceptLeft) {
            if (interceptRight) PhotoViewPager.InterceptType.BOTH
            else PhotoViewPager.InterceptType.LEFT
        } else if (interceptRight) {
            PhotoViewPager.InterceptType.RIGHT
        } else {
            PhotoViewPager.InterceptType.NONE
        }
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
        return binding.photoViewPager.currentItem
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
        menuInflater.inflate(R.menu.photo_view_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_current -> {
                PhotoSaver.INSTANCE.onSave(pagerAdapter.photoInfos[getCurrentPosition()])
                true
            }
            R.id.save_all -> {
                PhotoSaver.INSTANCE.onSave(*pagerAdapter.photoInfos.toTypedArray())
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }

    override fun getSystemService(name: String): Any? {
        return when (name) {
            WINDOW_SERVICE, SEARCH_SERVICE, LAYOUT_INFLATER_SERVICE -> super.getSystemService(name)
            else -> applicationContext.getSystemService(name)
        }
    }
}