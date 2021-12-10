package com.hym.zhankukotlin.ui

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.LinearLayoutCompat
import com.hym.zhankukotlin.databinding.PagedLayoutBinding

/**
 * @author hehua2008
 * @date 2021/12/10
 */
class PagedLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {
    private lateinit var binding: PagedLayoutBinding
    var activePage: Int = 1
        set(value) {
            field = value
            binding.activePage.text = "$value"
            onUpdatePage()
        }
    var lastPage: Int = 2
        set(value) {
            field = value
            binding.lastPage.text = "$value"
            onUpdatePage()
        }

    override fun onFinishInflate() {
        super.onFinishInflate()
        binding = PagedLayoutBinding.bind(this)
    }

    private fun onUpdatePage() {
        visibility = if (activePage == 1 && lastPage == 1) GONE else VISIBLE
        binding.prePage.visibility = if (activePage > 1) VISIBLE else GONE
        binding.nextPage.visibility = if (activePage == lastPage) GONE else VISIBLE
        binding.numberEdit.setText("${(activePage + 1).coerceAtMost(lastPage)}")
    }
}