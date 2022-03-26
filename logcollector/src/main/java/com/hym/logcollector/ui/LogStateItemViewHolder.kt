package com.hym.logcollector.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.RecyclerView
import com.hym.logcollector.databinding.LogStateItemBinding

/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
internal class LogStateItemViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LogStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false).root
) {
    private val binding = LogStateItemBinding.bind(itemView)

    fun bindTo(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is Loading
        binding.retryButton.isVisible = false /*loadState is Error*/
        binding.errorMsg.isVisible = !(loadState as? Error)?.error?.message.isNullOrBlank()
        binding.errorMsg.text = (loadState as? Error)?.error?.message
    }
}