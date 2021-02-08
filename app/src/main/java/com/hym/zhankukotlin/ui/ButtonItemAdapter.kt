package com.hym.zhankukotlin.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.RecycledViewPool
import com.google.android.flexbox.FlexboxLayoutManager
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.R
import com.hym.zhankukotlin.databinding.ButtonItemBinding
import com.hym.zhankukotlin.ui.ButtonGroupRecyclerView.ButtonCheckedAdapter

abstract class ButtonItemAdapter : ButtonCheckedAdapter<BindingViewHolder<ButtonItemBinding>>() {
    companion object {
        @JvmStatic
        val buttonRecyclerPool = RecycledViewPool()
        protected const val BUTTON_ITEM_TYPE = 1

        init {
            buttonRecyclerPool.setMaxRecycledViews(BUTTON_ITEM_TYPE, 60)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return BUTTON_ITEM_TYPE
    }

    @CallSuper
    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ): BindingViewHolder<ButtonItemBinding> {
        val binding: ButtonItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(MyApplication.INSTANCE), R.layout.button_item, parent, false
        )
        return BindingViewHolder(binding)
    }

    @CallSuper
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setRecycledViewPool(buttonRecyclerPool)
        when (val layoutManager = recyclerView.layoutManager) {
            is LinearLayoutManager -> layoutManager.recycleChildrenOnDetach = true
            is FlexboxLayoutManager -> layoutManager.recycleChildrenOnDetach = true
        }
    }

    @CallSuper
    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        recyclerView.setRecycledViewPool(null)
    }
}