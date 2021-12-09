package com.hym.zhankukotlin.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.model.WorkDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    private val _detailWorkId = MutableLiveData<String>()
    val detailWorkId: LiveData<String> = _detailWorkId

    private val _detailItem = MutableLiveData<WorkDetails>()
    val detailItem: LiveData<WorkDetails> = _detailItem

    fun setDetailWorkId(workId: String) {
        if (_detailWorkId.value == workId) return
        _detailWorkId.value = workId
    }

    fun getDetailFromNetwork() {
        val work: String = detailWorkId.value ?: return
        viewModelScope.launch(Dispatchers.IO) {
            try {
                MyApplication.networkService.getWorkDetails(work).run {
                    dataContent.also {
                        if (it == null) Log.e(TAG, "getWorkDetails $work failed: $msg")
                        else _detailItem.postValue(it)
                    }
                }
            } catch (t: Throwable) {
                Log.e(TAG, "getWorkDetails $work failed", t)
            }
        }
    }
}