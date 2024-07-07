package com.hym.zhankumultiplatform

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hym.zhankumultiplatform.di.GlobalComponent
import com.hym.zhankumultiplatform.model.TopCate
import com.hym.zhankumultiplatform.util.Logger
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.async

object MyAppViewModel : ViewModel() {
    private const val TAG = "MyAppViewModel"

    var categoryItems by mutableStateOf<ImmutableList<TopCate>>(persistentListOf())
        private set

    fun getCategoryItemsFromNetworkAsync(): Deferred<Unit> = viewModelScope.async(Dispatchers.IO) {
        try {
            GlobalComponent.Instance.networkService.getAllCategoryListContainArticle().run {
                dataContent.also {
                    if (it == null) Logger.e(TAG, "getCategoryItemList failed: $msg")
                    else categoryItems = it.toImmutableList()
                }
            }
        } catch (t: Throwable) {
            Logger.e(TAG, "getCategoryItemList failed", t)
        }
    }
}

inline fun <reified VM : ViewModel> getAppViewModel() = MyAppViewModel
