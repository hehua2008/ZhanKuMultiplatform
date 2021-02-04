package com.hym.zhankukotlin.ui.detail

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.network.DetailItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailViewModel : ViewModel() {
    val detailUrl = MutableLiveData<String?>()
    val detailItem = MutableLiveData<DetailItem?>()

    fun setDetailUrl(url: String?) {
        if (url != null && url != detailUrl.value) {
            detailUrl.value = url
        }
    }

    fun getDetailFromNetwork() {
        val url: String = detailUrl.value ?: return
        viewModelScope.launch {
            val item = withContext(Dispatchers.IO) {
                try {
                    return@withContext MyApplication.networkService.getDetailItem(url)
                } catch (t: Throwable) {
                    Log.e(TAG, "getDetailItem $url failed", t)
                    return@withContext null
                }
            }
            if (item != null) {
                detailItem.value = item
            }
        }
    }

    companion object {
        private val TAG = DetailViewModel::class.java.simpleName
    }
}