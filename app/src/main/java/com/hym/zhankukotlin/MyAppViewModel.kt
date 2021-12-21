package com.hym.zhankukotlin

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hym.zhankukotlin.model.TopCate
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MyAppViewModel(app: Application) : AndroidViewModel(app) {
    companion object {
        private const val TAG = "MyAppViewModel"
    }

    private val _categoryItems = MutableLiveData<List<TopCate>>()
    val categoryItems: LiveData<List<TopCate>> = _categoryItems

    fun getCategoryItemsFromNetworkAsync(): Deferred<Unit> = viewModelScope.async(Dispatchers.IO) {
        try {
            MyApplication.networkService.getAllCategoryListContainArticle().run {
                dataContent.also {
                    if (it == null) Log.e(TAG, "getCategoryItemList failed: $msg")
                    else _categoryItems.postValue(it)
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "getCategoryItemList failed", t)
        }
    }
}