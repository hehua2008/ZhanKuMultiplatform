package com.hym.zhankukotlin.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.network.CategoryItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SectionsPagerViewModel : ViewModel() {
    val categoryItems = MutableLiveData<List<CategoryItem>>()

    fun getCategoryItemsFromNetwork() {
        viewModelScope.launch {
            val items = withContext(Dispatchers.IO) {
                try {
                    return@withContext MyApplication.networkService.getCategoryItemList()
                } catch (t: Throwable) {
                    Log.e(TAG, "getCategoryItemList failed", t)
                    return@withContext null
                }
            }
            if (items != null) {
                categoryItems.value = items
            }
        }
    }

    companion object {
        private val TAG = SectionsPagerViewModel::class.simpleName
    }
}