package com.hym.zhankukotlin.ui.detail

import android.Manifest
import android.graphics.Rect
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.flexbox.JustifyContent
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.DetailHeaderLayoutBinding
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.model.WorkDetails
import com.hym.zhankukotlin.ui.BindingViewHolder
import com.hym.zhankukotlin.util.PermissionUtils
import com.hym.zhankukotlin.util.PictureUtils
import com.hym.zhankukotlin.util.ViewUtils.getActivity

class DetailHeaderAdapter(
    recyclerView: RecyclerView,
    private val mTitle: String,
    private val mContentType: Int,
    private val mContentId: String
) : RecyclerView.Adapter<BindingViewHolder<DetailHeaderLayoutBinding>>() {
    private val binding = DetailHeaderLayoutBinding.inflate(
        LayoutInflater.from(recyclerView.context),
        recyclerView,
        false
    ).apply {
        detailTitle.text = mTitle
        detailLink.text = StringBuilder("https://www.zcool.com.cn/").apply {
            when (mContentType) {
                Content.CONTENT_TYPE_ARTICLE -> append("article/")
                Content.CONTENT_TYPE_WORK -> append("work/")
            }
            append(mContentId)
        }

        val tagItemLayoutManager =
            FlexboxLayoutManager(recyclerView.context, FlexDirection.ROW, FlexWrap.WRAP)
        tagItemLayoutManager.justifyContent = JustifyContent.SPACE_EVENLY
        tagItemRecycler.layoutManager = tagItemLayoutManager
        tagItemRecycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private val mOffset = recyclerView.resources.getDimensionPixelSize(
                R.dimen.button_item_horizontal_offset
            ) and 1.inv()
            private val mHalfOffset = mOffset shr 1

            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                val itemPosition =
                    (view.layoutParams as RecyclerView.LayoutParams).viewLayoutPosition
                val itemCount = state.itemCount
                val left = if (itemPosition == 0) mOffset else mHalfOffset
                val right = if (itemPosition == itemCount - 1) mOffset else mHalfOffset
                outRect.set(left, 0, right, 0)
            }
        })
        tagItemRecycler.adapter = TagUrlItemAdapter()
    }

    override fun getItemCount(): Int = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = BindingViewHolder(binding)

    override fun onBindViewHolder(
        holder: BindingViewHolder<DetailHeaderLayoutBinding>,
        position: Int
    ) = Unit

    fun setWorkDetails(workDetails: WorkDetails) {
        binding.run {
            root.setWorkDetails(workDetails)

            (tagItemRecycler.adapter as TagUrlItemAdapter).setTagItems(workDetails.product.run {
                listOf(fieldCateObj, subCateObj)
            })

            downloadAll.setOnClickListener {
                val activity = root.getActivity()
                if (activity == null) {
                    Toast.makeText(
                        MyApplication.INSTANCE, R.string.download_failed, Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && !PermissionUtils.checkSelfPermission(
                        activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                ) {
                    Toast.makeText(activity, R.string.permission_denied, Toast.LENGTH_LONG).show()
                    PermissionUtils.requestPermissions(
                        activity, Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    return@setOnClickListener
                }
                PictureUtils.downloadAll(workDetails.product.productImages.map { it.oriUrl })
            }

            GlideApp.with(root)
                .load(workDetails.product.creatorObj.avatar1x)
                .into(detailAvatar)
        }
    }
}