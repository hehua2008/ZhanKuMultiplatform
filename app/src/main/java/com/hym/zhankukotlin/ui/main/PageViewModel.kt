package com.hym.zhankukotlin.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.network.Order
import com.hym.zhankukotlin.network.PreviewItem
import com.hym.zhankukotlin.network.PreviewResult
import com.hym.zhankukotlin.paging.PreviewPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PageViewModel : ViewModel() {
    val previewUrl = MutableLiveData<String?>()
    val previewResult = MutableLiveData<PreviewResult?>()
    val pagingFlow = MutableLiveData<Flow<PagingData<PreviewItem>>?>()

    private var mPage = 1
    private var mSubcat = "0!0!"
    private var mOrder = Order.EDITOR_CHOICE

    fun setUrl(url: String?) {
        if (url === null || url == previewUrl.value) {
            return
        }
        val pageStart = url.lastIndexOf('!') + 1
        if (pageStart > 0) {
            try {
                mPage = url.substring(pageStart).toInt()
            } catch (e: NumberFormatException) {
                mPage = 1
                Log.w(TAG, "parse int failed", e)
            }
        } else {
            mPage = 1
        }
        previewUrl.value = url
        // TODO save order
        updatePreviewItemFromNetwork()
    }

    fun setPage(page: Int) {
        if (mPage == page) {
            return
        }
        mPage = page
        var url = previewUrl.value ?: return
        val pageStart = url.lastIndexOf('!') + 1
        if (pageStart <= 0) {
            return
        }
        url = url.substring(0, pageStart) + mPage
        setUrl(url)
    }

    fun setSubcat(subcat: String) {
        if (mSubcat == subcat) {
            return
        }
        mSubcat = subcat
        var url = previewUrl.value ?: return
        val first = url.indexOf('!')
        if (first < 0 || first == url.length - 1) {
            return
        }
        val second = url.indexOf('!', first + 1)
        if (second < 0 || second == url.length - 1) {
            return
        }
        val third = url.indexOf('!', second + 1)
        if (third < 0) {
            return
        }
        url = url.substring(0, first + 1) + mSubcat + url.substring(third + 1)
        setUrl(url)
    }

    fun setOrder(order: Order) {
        if (mOrder == order) {
            return
        }
        mOrder = order
        var url = previewUrl.value ?: return
        val firstToLast = url.lastIndexOf('!')
        if (firstToLast <= 0) {
            return
        }
        val secondToLast = url.lastIndexOf('!', firstToLast - 1)
        if (secondToLast <= 0) {
            return
        }
        val thirdToLast = url.lastIndexOf('!', secondToLast - 1)
        if (thirdToLast <= 0) {
            return
        }
        url = url.substring(0, thirdToLast + 1) + mOrder.path + mPage
        setUrl(url)
    }

    private fun updatePreviewItemFromNetwork() {
        val url = previewUrl.value ?: return
        viewModelScope.launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    return@withContext MyApplication.networkService.getPreviewResult(url)
                } catch (t: Throwable) {
                    Log.e(TAG, "getPreviewResult $url failed", t)
                    return@withContext null
                }
            }
            if (result != null) {
                previewResult.value = result
                updatePager(url)
            }
        }
    }

    private fun updatePager(url: String) {
        val newFlow = Pager(
                // Configure how data is loaded by passing additional properties to
                // PagingConfig, such as prefetchDistance.
                config = PagingConfig(pageSize = 25),
                initialKey = url,
                pagingSourceFactory = { PreviewPagingSource(MyApplication.networkService) }
        )
                .flow
                .cachedIn(viewModelScope)

        pagingFlow.value = newFlow
    }

    companion object {
        private val TAG = PageViewModel::class.java.simpleName
    }
}