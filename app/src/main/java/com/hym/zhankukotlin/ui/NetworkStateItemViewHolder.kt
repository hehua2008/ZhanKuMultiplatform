package com.hym.zhankukotlin.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadState.Error
import androidx.paging.LoadState.Loading
import androidx.recyclerview.widget.RecyclerView
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.databinding.NetworkStateItemBinding

/**
 * A View Holder that can display a loading or have click action.
 * It is used to show the network state of paging.
 */
class NetworkStateItemViewHolder(
    parent: ViewGroup, private val retryCallback: () -> Unit
) : RecyclerView.ViewHolder(
    NetworkStateItemBinding.inflate(LayoutInflater.from(MyApplication.INSTANCE), parent, false).root
) {
    private val binding = NetworkStateItemBinding.bind(itemView)
        .apply {
            retryButton.setOnClickListener { retryCallback() }
        }

    fun bindTo(loadState: LoadState) {
        binding.progressBar.isVisible = loadState is Loading
        binding.retryButton.isVisible = loadState is Error
        binding.errorMsg.isVisible = !(loadState as? Error)?.error?.message.isNullOrBlank()
        binding.errorMsg.text = (loadState as? Error)?.error?.message
    }
}
