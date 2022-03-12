package com.hym.zhankukotlin.ui.detail

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityDetailBinding
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.model.PhotoInfo
import com.hym.zhankukotlin.ui.ThemeColorRetriever.setThemeColor
import com.hym.zhankukotlin.ui.photoview.PhotoViewActivity
import com.hym.zhankukotlin.util.MMCQ
import com.hym.zhankukotlin.util.createOverrideContext
import com.hym.zhankukotlin.util.isNightMode

class DetailActivity : AppCompatActivity() {
    companion object {
        const val KEY_TITLE = "TITLE"
        const val KEY_CONTENT_TYPE = "CONTENT_TYPE"
        const val KEY_CONTENT_ID = "CONTENT_ID"
        const val KEY_COLOR = "COLOR"
    }

    private lateinit var mTitle: String
    private lateinit var mContentId: String
    private var mContentType = Content.CONTENT_TYPE_WORK

    private lateinit var binding: ActivityDetailBinding
    private lateinit var detailViewModel: DetailViewModel
    private lateinit var photoViewActivityLauncher: ActivityResultLauncher<Pair<List<PhotoInfo>, Int>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTitle = intent.getStringExtra(KEY_TITLE)!!
        mContentId = intent.getStringExtra(KEY_CONTENT_ID)!!
        mContentType = intent.getIntExtra(KEY_CONTENT_TYPE, Content.CONTENT_TYPE_WORK)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val themeColor = intent.getParcelableExtra(KEY_COLOR) as? MMCQ.ThemeColor
        setThemeColor(themeColor)
        themeColor?.isDarkText?.let {
            ViewCompat.getWindowInsetsController(window.decorView)?.isAppearanceLightStatusBars = it
        }
        binding.actionBar.run {
            title = mTitle
            val isNightMode = resources.configuration.isNightMode()
            if (isNightMode == themeColor?.isDarkText) {
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
                outRect.set(
                    0, if (position == 0) 0 else mOffset,
                    0, if (lastPosition != 0 && position == lastPosition) mBottomOffset else 0
                )
            }
        })

        detailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]

        val detailHeaderAdapter =
            DetailHeaderAdapter(binding.detailRecycler, mTitle, mContentType, mContentId)
        val detailVideoAdapter = DetailVideoAdapter(detailViewModel.playerProvider)
        val detailImageAdapter = DetailImageAdapter()
        binding.detailRecycler.adapter =
            ConcatAdapter(detailHeaderAdapter, detailVideoAdapter, detailImageAdapter)

        detailViewModel.workDetails.observe(this) { workDetails ->
            binding.swipeRefresh.isRefreshing = false
            workDetails ?: return@observe
            detailHeaderAdapter.setWorkDetails(workDetails)
            detailVideoAdapter.submitList(workDetails.product.productVideos)
            detailImageAdapter.submitList(workDetails.product.productImages)
        }

        initPhotoViewActivityLauncher()

        loadData()
    }

    private fun loadData() {
        detailViewModel.setDetailTypeAndId(mContentType, mContentId)
    }

    private fun initPhotoViewActivityLauncher() {
        val contract = object : ActivityResultContract<Pair<List<PhotoInfo>, Int>, Int?>() {
            override fun createIntent(
                context: Context,
                input: Pair<List<PhotoInfo>, Int>
            ): Intent {
                return Intent(context, PhotoViewActivity::class.java)
                    .putParcelableArrayListExtra(
                        PhotoViewActivity.PHOTO_INFOS,
                        ArrayList(input.first)
                    )
                    .putExtra(PhotoViewActivity.CURRENT_POSITION, input.second)
            }

            override fun parseResult(resultCode: Int, intent: Intent?): Int? {
                return intent?.getIntExtra(PhotoViewActivity.CURRENT_POSITION, 0)
            }
        }

        photoViewActivityLauncher = registerForActivityResult(contract) {
            it ?: return@registerForActivityResult
            binding.detailRecycler.scrollToPosition(it + 1)
        }
    }

    fun launchPhotoViewActivity(photoInfos: List<PhotoInfo>, position: Int) {
        photoViewActivityLauncher.launch(
            photoInfos to position,
            ActivityOptionsCompat.makeCustomAnimation(this, 0, android.R.anim.fade_out)
        )
    }
}