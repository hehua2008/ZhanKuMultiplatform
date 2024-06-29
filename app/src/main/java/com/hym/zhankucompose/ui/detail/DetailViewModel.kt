package com.hym.zhankucompose.ui.detail

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.LoadState
import com.hym.zhankucompose.di.GlobalComponent
import com.hym.zhankucompose.model.ArticleDetails
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.WorkDetails
import com.hym.zhankucompose.network.NetworkService
import com.hym.zhankucompose.player.PlayerProvider
import com.hym.zhankucompose.util.MMCQ
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class DetailViewModel(private val networkService: NetworkService = GlobalComponent.Instance.networkService) :
    ViewModel() {
    companion object {
        private const val TAG = "DetailViewModel"
        private val NotLoading = LoadState.NotLoading(false)
    }

    var workDetails by mutableStateOf<WorkDetails?>(null)
        private set

    var articleDetails by mutableStateOf<ArticleDetails?>(null)
        private set

    var themeColor by mutableStateOf<MMCQ.ThemeColor?>(null)

    val playerProvider = PlayerProvider()

    var loadState by mutableStateOf<LoadState>(NotLoading)
        private set

    var position by mutableStateOf<Int?>(null)

    fun setDetailTypeAndId(type: ContentType, id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                when (type) {
                    ContentType.WORK -> {
                        loadState = LoadState.Loading

                        networkService.getWorkDetails(id).run {
                            dataContent.also {
                                loadState = if (it == null) {
                                    val errorMsg = "getWorkDetails $id failed: $msg"
                                    Log.e(TAG, errorMsg)
                                    LoadState.Error(Exception(errorMsg))
                                } else {
                                    workDetails = it
                                    NotLoading
                                }
                            }
                        }
                    }

                    ContentType.ARTICLE -> {
                        loadState = LoadState.Loading

                        networkService.getArticleDetails(id).run {
                            dataContent.also {
                                loadState = if (it == null) {
                                    val errorMsg = "getArticleDetails $id failed: $msg"
                                    Log.e(TAG, errorMsg)
                                    LoadState.Error(Exception(errorMsg))
                                } else {
                                    articleDetails = it
                                    NotLoading
                                }
                            }
                        }
                    }
                }
            } catch (t: Throwable) {
                Log.e(TAG, "setDetailTypeAndId $type $id failed", t)
                workDetails = null
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