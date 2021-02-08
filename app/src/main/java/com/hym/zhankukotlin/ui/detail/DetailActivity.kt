package com.hym.zhankukotlin.ui.detail

import android.Manifest
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ActivityDetailBinding
import com.hym.zhankukotlin.util.PermissionUtils
import com.hym.zhankukotlin.util.PictureUtils


class DetailActivity : AppCompatActivity() {
    private lateinit var mTitle: String
    private lateinit var mUrl: String

    private lateinit var mBinding: ActivityDetailBinding
    private lateinit var mDetailViewModel: DetailViewModel
    private lateinit var mTagItemLayoutManager: FlexboxLayoutManager
    private lateinit var mTagUrlItemAdapter: TagUrlItemAdapter
    private lateinit var mDetailImageAdapter: DetailImageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mTitle = intent.getStringExtra(KEY_TITLE)!!
        mUrl = intent.getStringExtra(KEY_URL)!!
        title = mTitle

        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)
        mBinding.detailTitle.text = mTitle
        mBinding.detailLink.text = mUrl

        mTagItemLayoutManager = FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP)
        mTagItemLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        mBinding.tagItemRecycler.layoutManager = mTagItemLayoutManager
        mBinding.tagItemRecycler.addItemDecoration(object : ItemDecoration() {
            private val mOffset = resources.getDimensionPixelSize(
                R.dimen.button_item_horizontal_offset
            ) and 1.inv()
            private val mHalfOffset = mOffset shr 1

            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                val itemPosition =
                    (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val itemCount = state.itemCount
                val left = if (itemPosition == 0) mOffset else mHalfOffset
                val right = if (itemPosition == itemCount - 1) mOffset else mHalfOffset
                outRect.set(left, 0, right, 0)
            }
        })
        mTagUrlItemAdapter = TagUrlItemAdapter()
        mBinding.tagItemRecycler.adapter = mTagUrlItemAdapter

        mBinding.detailRecycler.addItemDecoration(object : ItemDecoration() {
            private val mOffset = resources.getDimensionPixelSize(R.dimen.common_vertical_margin)

            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.set(0, mOffset, 0, 0)
            }
        })
        mDetailImageAdapter = DetailImageAdapter()
        mBinding.detailRecycler.adapter = mDetailImageAdapter

        mDetailViewModel = ViewModelProvider(this).get(DetailViewModel::class.java)
        mDetailViewModel.detailUrl.observe(
            this,
            Observer { mDetailViewModel.getDetailFromNetwork() })
        mDetailViewModel.detailItem.observe(this, Observer { detailItem ->
            mBinding.detailItem = detailItem ?: return@Observer
            mTagUrlItemAdapter.setTagItems(detailItem.categorys)
            mDetailImageAdapter.setImgUrls(detailItem.imgUrls)
            mBinding.downloadAll.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
                    && !PermissionUtils.checkSelfPermission(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    Toast.makeText(this, R.string.permission_denied, Toast.LENGTH_LONG).show()
                    PermissionUtils.requestPermissions(
                        this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    return@setOnClickListener
                }
                PictureUtils.downloadAll(detailItem.imgUrls)
            }
        })

        mDetailViewModel.setDetailUrl(mUrl)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val KEY_TITLE = "TITLE"
        const val KEY_URL = "URL"
    }
}