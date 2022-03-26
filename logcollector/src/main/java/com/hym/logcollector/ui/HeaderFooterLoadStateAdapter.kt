package com.hym.logcollector.ui

import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter

internal class HeaderFooterLoadStateAdapter : LoadStateAdapter<LogStateItemViewHolder>() {
    override fun onBindViewHolder(holder: LogStateItemViewHolder, loadState: LoadState) {
        holder.bindTo(loadState)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): LogStateItemViewHolder = LogStateItemViewHolder(parent)
}