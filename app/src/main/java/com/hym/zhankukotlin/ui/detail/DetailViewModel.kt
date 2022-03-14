package com.hym.zhankukotlin.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.model.ContentType
import com.hym.zhankukotlin.model.WorkDetails
import com.hym.zhankukotlin.player.PlayerProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailViewModel : ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    private val _workDetails = MutableLiveData<WorkDetails?>()
    val workDetails: LiveData<WorkDetails?> = _workDetails

    val playerProvider = PlayerProvider()

    fun setDetailTypeAndId(type: Int, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (type) {
                    ContentType.WORK.value -> {
                        MyApplication.networkService.getWorkDetails(id).run {
                            dataContent.also {
                                if (it == null) Log.e(TAG, "getWorkDetails $id failed: $msg")
                                _workDetails.postValue(it)
                            }
                        }
                    }
                    ContentType.ARTICLE.value -> {
                        // TODO: 2021/12/29
                        _workDetails.postValue(null)
                    }
                }
            } catch (t: Throwable) {
                Log.e(TAG, "setDetailTypeAndId $type $id failed", t)
                _workDetails.postValue(null)
            }
        }
    }

    override fun onCleared() {
        // Post playerProvider.onCleared() action after ViewModel.onCleared() (Activity.onDestroy())
        GlobalScope.launch(Dispatchers.Main) {
            playerProvider.onCleared()
        }
    }
}