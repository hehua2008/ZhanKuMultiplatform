package com.hym.zhankukotlin.ui.detail

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hym.zhankukotlin.BaseActivity
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityDetailBinding
import com.hym.zhankukotlin.model.ContentType
import com.hym.zhankukotlin.ui.ThemeColorRetriever
import com.hym.zhankukotlin.ui.ThemeColorRetriever.setThemeColor
import com.hym.zhankukotlin.ui.photoviewer.PhotoViewerActivity
import com.hym.zhankukotlin.ui.photoviewer.UrlPhotoInfo
import com.hym.zhankukotlin.util.MMCQ
import com.hym.zhankukotlin.util.createOverrideContext
import com.hym.zhankukotlin.util.isNightMode
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DetailActivity : BaseActivity() {
    companion object {
        const val KEY_TITLE = "TITLE"
        const val KEY_CONTENT_TYPE = "CONTENT_TYPE"
        const val KEY_CONTENT_ID = "CONTENT_ID"
        const val KEY_COLOR = "COLOR"
    }

    private lateinit var mTitle: String
    private lateinit var mContentId: String
    private var mContentType = ContentType.WORK.value
    private var mThemeColor: MMCQ.ThemeColor? = null

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var detailContentAdapter: DetailContentAdapter
    private lateinit var photoViewerActivityLauncher: ActivityResultLauncher<Pair<List<UrlPhotoInfo>, Int>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (intent.action == Intent.ACTION_VIEW) {
            val data = intent.data!!
            mTitle = ""
            mContentId = data.lastPathSegment!!
            mContentType =
                if (data.pathSegments[0] == "work") ContentType.WORK.value else ContentType.ARTICLE.value
        } else {
            mTitle = intent.getStringExtra(KEY_TITLE)!!
            mContentId = intent.getStringExtra(KEY_CONTENT_ID)!!
            mContentType = intent.getIntExtra(KEY_CONTENT_TYPE, mContentType)
        }

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        (intent.getParcelableExtra(KEY_COLOR) as? MMCQ.ThemeColor)?.let { updateThemeColor(it) }
        binding.actionBar.run {
            title = mTitle
            updateNavigationIcon()
            setNavigationOnClickListener { finish() }
        }
        binding.swipeRefresh.setOnRefreshListener { loadData() }
        binding.detailRecycler.addItemDecoration(object : ItemDecoration() {
            private val mOffset = resources.getDimensionPixelSize(R.dimen.common_vertical_margin)
            private val mBottomOffset =
                resources.getDimensionPixelSize(R.dimen.detail_img_bottom_margin)

            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val position = (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val lastPosition = state.itemCount - 1
                val bottomOffset = if (lastPosition != 0 && position == lastPosition) {
                    val image =
                        detailContentAdapter.currentList.getOrNull(position - 1) as? DetailImage
                    image?.data?.let {
                        if (it.width == 0 || it.height == 0) return@let mBottomOffset
                        val imageHeight = (it.height * parent.width / it.width.toFloat()).toInt()
                        mBottomOffset.coerceAtLeast((window.decorView.height - imageHeight) / 2)
                    } ?: mOffset
                } else mOffset
                outRect.set(0, 0, 0, bottomOffset)
            }
        })

        detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        val detailHeaderAdapter =
            DetailHeaderAdapter(binding.detailRecycler, mTitle, mContentType, mContentId)
        detailContentAdapter = DetailContentAdapter(detailViewModel.playerProvider)
        binding.detailRecycler.adapter = ConcatAdapter(detailHeaderAdapter, detailContentAdapter)

        detailViewModel.workDetails.observe(this) { workDetails ->
            binding.swipeRefresh.isRefreshing = false
            workDetails ?: return@observe
            mTitle = workDetails.product.title
            binding.actionBar.title = mTitle
            detailHeaderAdapter.updateTitle(mTitle)
            detailHeaderAdapter.setWorkDetails(workDetails)
            val detailContents = workDetails.product.productVideos.map {
                DetailVideo(it)
            } + workDetails.product.productImages.map {
                DetailImage(it)
            }
            detailContentAdapter.submitList(detailContents)
            if (mThemeColor != null) return@observe
            val firstImage = workDetails.product.productImages.firstOrNull() ?: return@observe
            GlideApp.with(this).run {
                asBitmap()
                    .load(firstImage.url)
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(
                            resource: Bitmap,
                            transition: Transition<in Bitmap>?
                        ) {
                            val target = this
                            lifecycleScope.launch {
                                ThemeColorRetriever.getMainThemeColor(resource)?.let {
                                    updateThemeColor(it)
                                    updateNavigationIcon()
                                }
                                clear(target)
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) = Unit
                    })
            }
        }

        detailViewModel.articleDetails.observe(this) { articleDetails ->
            binding.swipeRefresh.isRefreshing = false
            articleDetails ?: return@observe
            mTitle = articleDetails.articledata.title
            binding.actionBar.title = mTitle
            detailHeaderAdapter.updateTitle(mTitle)
            val detailContents =
                DetailContent.articleDetailsToDetailContent(articleDetails)
            val images = detailContents.filterIsInstance<DetailImage>().map { it.data }
            detailHeaderAdapter.setArticleDetails(articleDetails, images)
            detailContentAdapter.submitList(detailContents)
        }

        initPhotoViewerActivityLauncher()

        loadData()
    }

    private fun updateThemeColor(themeColor: MMCQ.ThemeColor) {
        mThemeColor = themeColor
        setThemeColor(themeColor)
        ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars =
            themeColor.isDarkText
    }

    private fun updateNavigationIcon() {
        binding.actionBar.run {
            val isNightMode = resources.configuration.isNightMode()
            if (isNightMode != mThemeColor?.isDarkText) return@run
            val overrideContext = createOverrideContext(Configuration().apply {
                uiMode = (uiMode and Configuration.UI_MODE_NIGHT_MASK.inv()) or
                        (if (isNightMode) Configuration.UI_MODE_NIGHT_NO else Configuration.UI_MODE_NIGHT_YES)
            })
            val a = overrideContext.theme.obtainStyledAttributes(
                R.style.Widget_ZhanKuKotlin_Toolbar, R.styleable.Toolbar
            )
            navigationIcon = a.getDrawable(R.styleable.Toolbar_navigationIcon)
            a.recycle()
        }
    }

    private fun loadData() {
        detailViewModel.setDetailTypeAndId(mContentType, mContentId)
    }

    private fun initPhotoViewerActivityLauncher() {
        val contract =
            object : ActivityResultContract<Pair<List<UrlPhotoInfo>, Int>, Pair<Int, Rect?>?>() {
                override fun createIntent(
                    context: Context,
                    input: Pair<List<UrlPhotoInfo>, Int>
                ): Intent {
                    return Intent(context, PhotoViewerActivity::class.java)
                        .putParcelableArrayListExtra(
                            PhotoViewerActivity.PHOTO_INFOS,
                            ArrayList(input.first)
                        )
                        .putExtra(PhotoViewerActivity.CURRENT_POSITION, input.second)
                }

                override fun parseResult(resultCode: Int, intent: Intent?): Pair<Int, Rect?>? {
                    intent ?: return null
                    val position = intent.getIntExtra(PhotoViewerActivity.CURRENT_POSITION, 0)
                    val screenLocation =
                        intent.getParcelableExtra<Rect>(PhotoViewerActivity.SCREEN_LOCATION)
                    return position to screenLocation
                }
            }

        photoViewerActivityLauncher = registerForActivityResult(contract) { result ->
            result ?: return@registerForActivityResult
            val contentList = detailContentAdapter.currentList
            val detailImage = contentList.filterIsInstance<DetailImage>().getOrNull(result.first)
                ?: return@registerForActivityResult
            val screenLocation = result.second ?: window.decorView.run {
                IntArray(2).let {
                    getLocationOnScreen(it)
                    Rect(it[0], it[1], it[0] + width, it[1] + height)
                }
            }
            binding.detailRecycler.run {
                val image = detailImage.data
                val imageHeight = if (image.width == 0 || image.height == 0) 0
                else (image.height * width / image.width.toFloat()).toInt()
                val imageViewScreenTop =
                    screenLocation.top + (screenLocation.height() - imageHeight) / 2
                val recyclerViewScreenTop = IntArray(2).let {
                    getLocationOnScreen(it)
                    it[1]
                }
                val offset = imageViewScreenTop - recyclerViewScreenTop
                val position = 1 + contentList.indexOf(detailImage)
                (binding.detailRecycler.layoutManager as LinearLayoutManager)
                    .scrollToPositionWithOffset(position, offset)
            }
        }
    }

    fun launchPhotoViewerActivity(photoInfos: List<UrlPhotoInfo>, position: Int) {
        photoViewerActivityLauncher.launch(
            photoInfos to position,
            ActivityOptionsCompat.makeCustomAnimation(this, 0, android.R.anim.fade_out)
        )
    }
}