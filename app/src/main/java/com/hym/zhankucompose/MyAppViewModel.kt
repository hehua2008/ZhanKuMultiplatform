package com.hym.zhankucompose

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankucompose.di.GlobalComponent
import com.hym.zhankucompose.model.TopCate
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MyAppViewModel(private val app: Application) : AndroidViewModel(app) {
    companion object {
        private const val TAG = "MyAppViewModel"
    }

    var categoryItems by mutableStateOf<ImmutableList<TopCate>>(persistentListOf())
        private set

    fun getCategoryItemsFromNetworkAsync(): Deferred<Unit> = viewModelScope.async(Dispatchers.IO) {
        try {
            GlobalComponent.Instance.networkService.getAllCategoryListContainArticle().run {
                dataContent.also {
                    if (it == null) Log.e(TAG, "getCategoryItemList failed: $msg")
                    else categoryItems = it.toImmutableList()
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "getCategoryItemList failed", t)
        }
    }
}