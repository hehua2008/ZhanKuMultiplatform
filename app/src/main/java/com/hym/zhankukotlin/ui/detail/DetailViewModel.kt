package com.hym.zhankukotlin.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankukotlin.model.ArticleDetails
import com.hym.zhankukotlin.model.ContentType
import com.hym.zhankukotlin.model.WorkDetails
import com.hym.zhankukotlin.network.NetworkService
import com.hym.zhankukotlin.player.PlayerProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(private val networkService: NetworkService) :
    ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
    }

    private val _workDetails = MutableLiveData<WorkDetails?>()
    val workDetails: LiveData<WorkDetails?> = _workDetails

    private val _articleDetails = MutableLiveData<ArticleDetails?>()
    val articleDetails: LiveData<ArticleDetails?> = _articleDetails

    val playerProvider = PlayerProvider()

    fun setDetailTypeAndId(type: Int, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (type) {
                    ContentType.WORK.value -> {
                        networkService.getWorkDetails(id).run {
                            dataContent.also {
                                if (it == null) Log.e(TAG, "getWorkDetails $id failed: $msg")
                                _workDetails.postValue(it)
                            }
                        }
                    }
                    ContentType.ARTICLE.value -> {
                        networkService.getArticleDetails(id).run {
                            dataContent.also {
                                if (it == null) Log.e(TAG, "getArticleDetails $id failed: $msg")
                                _articleDetails.postValue(it)
                            }
                        }
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