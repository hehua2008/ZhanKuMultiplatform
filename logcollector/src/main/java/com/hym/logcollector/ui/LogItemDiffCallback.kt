package com.hym.logcollector.ui

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil
import com.hym.logcollector.impl.LogWrapper

/**
 * @author hehua2008
 * @date 2021/8/19
 */
object LogItemDiffCallback : DiffUtil.ItemCallback<LogWrapper>() {
    override fun areItemsTheSame(oldItem: LogWrapper, newItem: LogWrapper) =
        // lineNumber is unique.
        oldItem.lineNumber == newItem.lineNumber

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: LogWrapper, newItem: LogWrapper) =
        // Avoid time-consuming calculation of difference
        false
}