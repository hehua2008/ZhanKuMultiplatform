package com.hym.zhankukotlin.ui.detail

import android.graphics.Rect
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityDetailBinding
import com.hym.zhankukotlin.ui.ThemeColorRetriever.setThemeColor
import com.hym.zhankukotlin.util.MMCQ

class DetailActivity : AppCompatActivity() {
    companion object {
        const val KEY_TITLE = "TITLE"
        const val KEY_WORK_ID = "WORK_ID"
        const val KEY_COLOR = "COLOR"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mTitle = intent.getStringExtra(KEY_TITLE)!!
        val mWorkId = intent.getStringExtra(KEY_WORK_ID)!!

        val binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setThemeColor(intent.getParcelableExtra(KEY_COLOR) as? MMCQ.ThemeColor)
        binding.actionBar.run {
            title = mTitle
            setNavigationOnClickListener { finish() }
        }
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
                    0, if (position == lastPosition) mBottomOffset else 0
                )
            }
        })
        val detailHeaderAdapter = DetailHeaderAdapter(mTitle, mWorkId)
        val detailImageAdapter = DetailImageAdapter()
        binding.detailRecycler.adapter = ConcatAdapter(detailHeaderAdapter, detailImageAdapter)

        val mDetailViewModel = ViewModelProvider(this)[DetailViewModel::class.java]
        mDetailViewModel.detailWorkId.observe(this) { mDetailViewModel.getDetailFromNetwork() }
        mDetailViewModel.detailItem.observe(this) { detailItem ->
            detailItem ?: return@observe
            detailHeaderAdapter.setDetailItem(detailItem)
            detailImageAdapter.setImages(detailItem.product.productImages)
        }

        mDetailViewModel.setDetailWorkId(mWorkId)
    }
}