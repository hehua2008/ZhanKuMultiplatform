package com.hym.zhankucompose.ui

import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.hym.zhankucompose.ui.ButtonGroupRecyclerView.ButtonCheckedAdapter

abstract class ButtonItemAdapter : ButtonCheckedAdapter<ButtonItemAdapter.ViewHolder>() {
    companion object {
        protected const val BUTTON_ITEM_TYPE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return BUTTON_ITEM_TYPE
    }

    @CallSuper
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ButtonFactory.create(parent))
    }

    /*
    @CallSuper
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        when (val layoutManager = recyclerView.layoutManager) {
            is LinearLayoutManager -> layoutManager.recycleChildrenOnDetach = true
            is FlexboxLayoutManager -> layoutManager.recycleChildrenOnDetach = true
        }
    }
    */

    class ViewHolder(val button: MaterialButton) : RecyclerView.ViewHolder(button)
}