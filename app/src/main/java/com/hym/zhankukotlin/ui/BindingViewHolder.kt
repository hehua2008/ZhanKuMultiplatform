package com.hym.zhankukotlin.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BindingViewHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)