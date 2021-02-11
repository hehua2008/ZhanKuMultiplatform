package com.hym.zhankukotlin.ui.detail

import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.GlideAppExtension
import com.hym.zhankukotlin.GlideRequests
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.ui.ImageViewHeightListener
import com.hym.zhankukotlin.ui.ThemeColorListener

class DetailImageAdapter : RecyclerView.Adapter<DetailImageAdapter.ViewHolder>() {
    private var mRequestManager: GlideRequests? = null
    private var mImgUrls: List<String> = emptyList()
    private var mFirstBind = true

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /**
         * <androidx.constraintlayout.widget.ConstraintLayout
         *     android:layout_width="match_parent"
         *     android:layout_height="wrap_content">
         *
         *     <ImageView
         *         android:id="@+id/image_view"
         *         android:layout_width="match_parent"
         *         android:layout_height="0dp"
         *         android:adjustViewBounds="true"
         *         android:contentDescription="loading..."
         *         android:scaleType="fitCenter"
         *         app:layout_constraintDimensionRatio="1:10" />
         * </androidx.constraintlayout.widget.ConstraintLayout>
         */
        val context = parent.context
        val constraintLayout = ConstraintLayout(context)
        constraintLayout.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val imageView = ImageView(context).apply {
            id = R.id.image_view
            adjustViewBounds = true
            contentDescription = "Loading..."
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        imageView.layoutParams = ConstraintLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0).apply { dimensionRatio = "1:10" }
        constraintLayout.addView(imageView)
        return ViewHolder(constraintLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        mRequestManager!!
                .load(mImgUrls[position])
                .transparentPlaceHolder()
                .transition(GlideAppExtension.DRAWABLE_CROSS_FADE)
                //.originalSize()
                .addListener(ImageViewHeightListener)
                .apply {
                    if (mFirstBind && position == 0) {
                        mFirstBind = false
                        addListener(ThemeColorListener)
                    }
                }
                .into(holder.imageView)
                .waitForLayout()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onViewRecycled(holder: ViewHolder) {
        mRequestManager?.clear(holder.imageView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = GlideApp.with(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = null
    }

    override fun getItemCount(): Int {
        return mImgUrls.size
    }

    fun setImgUrls(imgUrls: List<String>) {
        mImgUrls = imgUrls
        notifyDataSetChanged()
    }

    class ViewHolder(val viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup) {
        val imageView: ImageView = viewGroup.findViewById(R.id.image_view)
    }
}