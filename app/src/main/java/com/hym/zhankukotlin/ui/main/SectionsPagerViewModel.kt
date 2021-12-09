package com.hym.zhankukotlin.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.model.TopCate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SectionsPagerViewModel : ViewModel() {
    companion object {
        private const val TAG = "SectionsPagerViewModel"
    }

    private val _categoryItems = MutableLiveData<List<TopCate>>()
    val categoryItems: LiveData<List<TopCate>> = _categoryItems

    fun getCategoryItemsFromNetwork() {
        viewModelScope.launch(Dispatchers.IO) {
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
}