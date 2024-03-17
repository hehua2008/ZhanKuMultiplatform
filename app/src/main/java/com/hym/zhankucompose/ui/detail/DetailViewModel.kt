package com.hym.zhankucompose.ui.detail

import android.graphics.Rect
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.hym.zhankucompose.model.ArticleDetails
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.WorkDetails
import com.hym.zhankucompose.network.NetworkService
import com.hym.zhankucompose.player.PlayerProvider
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
        private val NotLoading = LoadState.NotLoading(false)
    }

    private val _workDetails = MutableLiveData<WorkDetails?>()
    val workDetails: LiveData<WorkDetails?> = _workDetails

    private val _articleDetails = MutableLiveData<ArticleDetails?>()
    val articleDetails: LiveData<ArticleDetails?> = _articleDetails

    val playerProvider = PlayerProvider()

    var loadState by mutableStateOf<LoadState>(NotLoading)
        private set

    var positionAndScreenLocation by mutableStateOf<Pair<Int, Rect?>?>(null)

    fun setDetailTypeAndId(type: Int, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (type) {
                    ContentType.WORK.value -> {
                        loadState = LoadState.Loading

                        networkService.getWorkDetails(id).run {
                            dataContent.also {
                                loadState = if (it == null) {
                                    val errorMsg = "getWorkDetails $id failed: $msg"
                                    Log.e(TAG, errorMsg)
                                    LoadState.Error(Exception(errorMsg))
                                } else {
                                    _workDetails.postValue(it)
                                    NotLoading
                                }
                            }
                        }
                    }

                    ContentType.ARTICLE.value -> {
                        loadState = LoadState.Loading

                        networkService.getArticleDetails(id).run {
                            dataContent.also {
                                loadState = if (it == null) {
                                    val errorMsg = "getArticleDetails $id failed: $msg"
                                    Log.e(TAG, errorMsg)
                                    LoadState.Error(Exception(errorMsg))
                                } else {
                                    _articleDetails.postValue(it)
                                    NotLoading
                                }
                            }
                        }
                    }
                }
            } catch (t: Throwable) {
                Log.e(TAG, "setDetailTypeAndId $type $id failed", t)
                _workDetails.postValue(null)
                loadState = LoadState.Error(t)
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