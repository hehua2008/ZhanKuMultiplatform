package com.hym.zhankukotlin.ui.detail

import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.hym.zhankukotlin.GlideApp
import com.hym.zhankukotlin.GlideAppExtension
import com.hym.zhankukotlin.GlideRequests
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.model.ProductImage
import com.hym.zhankukotlin.ui.ImageViewHeightListener

class DetailImageAdapter : RecyclerView.Adapter<DetailImageAdapter.ViewHolder>() {
    private var mRequestManager: GlideRequests? = null
    private var mImages: List<ProductImage> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        /**
         * <androidx.constraintlayout.widget.ConstraintLayout
         *     android:layout_width="match_parent"
         *     android:layout_height="wrap_content">
         *
         *     <androidx.appcompat.widget.AppCompatImageView
         *         android:id="@+id/image_view"
         *         android:layout_width="match_parent"
         *         android:layout_height="0dp"
         *         android:adjustViewBounds="true"
         *         android:contentDescription="Loading..."
         *         android:scaleType="fitCenter"
         *         app:layout_constraintDimensionRatio="3:2" />
         * </androidx.constraintlayout.widget.ConstraintLayout>
         */
        val context = parent.context
        val constraintLayout = ConstraintLayout(context)
        constraintLayout.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        val imageView = AppCompatImageView(context).apply {
            id = R.id.image_view
            adjustViewBounds = true
            contentDescription = "Loading..."
            scaleType = ImageView.ScaleType.FIT_CENTER
        }
        imageView.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, 0
        ).apply { dimensionRatio = "3:2" }
        constraintLayout.addView(imageView)
        return ViewHolder(constraintLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val img = mImages[position]
        holder.imageView.run {
            layoutParams = (layoutParams as ConstraintLayout.LayoutParams).apply {
                dimensionRatio = "${img.width}:${img.height}"
            }
        }
        mRequestManager?.run {
            load(img.url)
                //.transparentPlaceHolder()
                .transition(GlideAppExtension.DRAWABLE_CROSS_FADE)
                //.originalSize()
                .addListener(ImageViewHeightListener)
                .into(holder.imageView)
                .waitForLayout()
        }
    }

    override fun getItemViewType(position: Int): Int = position

    override fun onViewRecycled(holder: ViewHolder) {
        mRequestManager?.clear(holder.imageView)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = GlideApp.with(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        mRequestManager = null
    }

    override fun getItemCount(): Int = mImages.size

    fun setImages(images: List<ProductImage>) {
        mImages = images
        notifyDataSetChanged()
    }

    class ViewHolder(viewGroup: ViewGroup) : RecyclerView.ViewHolder(viewGroup) {
        val imageView: AppCompatImageView = viewGroup.findViewById(R.id.image_view)
    }
}