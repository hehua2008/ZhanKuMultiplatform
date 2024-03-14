package com.hym.zhankucompose

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hym.zhankucompose.hilt.NetworkModule
import com.hym.zhankucompose.model.TopCate
import dagger.hilt.android.EntryPointAccessors
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MyAppViewModel(private val app: Application) : AndroidViewModel(app) {
    companion object {
        private const val TAG = "MyAppViewModel"
    }

    private val _categoryItems = MutableLiveData<ImmutableList<TopCate>>()
    val categoryItems: LiveData<ImmutableList<TopCate>> = _categoryItems

    fun getCategoryItemsFromNetworkAsync(): Deferred<Unit> = viewModelScope.async(Dispatchers.IO) {
        try {
            val accessor =
                EntryPointAccessors.fromApplication(app, NetworkModule.Accessor::class.java)
            accessor.networkService().getAllCategoryListContainArticle().run {
                dataContent.also {
                    if (it == null) Log.e(TAG, "getCategoryItemList failed: $msg")
                    else _categoryItems.postValue(it.toImmutableList())
                }
            }
        } catch (t: Throwable) {
            Log.e(TAG, "getCategoryItemList failed", t)
        }
    }
}