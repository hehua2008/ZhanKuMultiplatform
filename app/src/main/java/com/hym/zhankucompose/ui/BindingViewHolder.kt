package com.hym.zhankucompose.ui

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class BindingViewHolder<B : ViewBinding>(val binding: B) : RecyclerView.ViewHolder(binding.root)