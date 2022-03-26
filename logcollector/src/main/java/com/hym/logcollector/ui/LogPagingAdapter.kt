package com.hym.logcollector.ui

import android.text.util.Linkify
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hym.logcollector.base.LogLevel
import com.hym.logcollector.base.LogLevelColorMapper
import com.hym.logcollector.impl.LogWrapper

/**
 * @author hehua2008
 * @date 2021/8/22
 */
internal class LogPagingAdapter(private val mLogLevelColorMapper: LogLevelColorMapper) :
    PagingDataAdapter<LogWrapper, LogItemViewHolder>(LogItemDiffCallback) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogItemViewHolder {
        val textView = AppCompatTextView(parent.context)
        textView.layoutParams = RecyclerView.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.textSize = 10f
        textView.autoLinkMask = Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES
        return LogItemViewHolder(textView)
    }

    override fun onBindViewHolder(holder: LogItemViewHolder, position: Int) {
        val textView = holder.itemView as TextView
        val logWrapper = getItem(position)
        // Note that item may be null. ViewHolder must support binding a null item as a placeholder.
        textView.text = logWrapper ?: ""
        val color = mLogLevelColorMapper.map(logWrapper?.logLevel ?: LogLevel.DEFAULT)
        textView.setTextColor(color)
        textView.fixTextSelection()
    }

    private fun TextView.fixTextSelection() {
        setTextIsSelectable(false)
        post { setTextIsSelectable(true) }
    }
}