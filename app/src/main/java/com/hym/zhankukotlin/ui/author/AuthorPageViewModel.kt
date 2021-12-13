package com.hym.zhankukotlin.ui.author

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.model.SortOrder
import com.hym.zhankukotlin.paging.AuthorPagingSource
import com.hym.zhankukotlin.paging.LoadParamsHolder
import kotlinx.coroutines.flow.Flow

class AuthorPageViewModel(private val authorUid: Int) : ViewModel() {
    companion object {
        private const val TAG = "AuthorPageViewModel"
    }

    private val _page = MutableLiveData<Int>(1)
    val page: LiveData<Int> = _page

    private val _pageSize = MutableLiveData<Int>(25)
    val pageSize: LiveData<Int> = _pageSize

    private val _sortOrder = MutableLiveData<SortOrder>(SortOrder.LATEST_PUBLISH)
    val sortOrder: LiveData<SortOrder> = _sortOrder

    private val _mediatorLiveData = MediatorLiveData<Unit>().apply {
        addSource(page) { value = Unit }
        addSource(pageSize) { value = Unit }
        addSource(sortOrder) { value = Unit }
    }
    val mediatorLiveData: LiveData<Unit> = _mediatorLiveData

    fun setPage(page: Int) {
        if (page < 1 || _page.value == page) return
        _page.value = page
    }

    fun setPageSize(pageSize: Int) {
        if (_pageSize.value == pageSize) return
        _pageSize.value = pageSize
    }

    fun setSortOrder(sortOrder: SortOrder) {
        if (_sortOrder.value == sortOrder) return
        _sortOrder.value = sortOrder
    }

    val pagingFlow: Flow<PagingData<Content>> = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        config = PagingConfig(pageSize = pageSize.value ?: 25),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            AuthorPagingSource(
                networkService = MyApplication.networkService,
                authorUid = authorUid,
                initialPage = page.value ?: 1,
                pageSize = pageSize.value ?: 25,
                sortOrder = sortOrder.value ?: SortOrder.LATEST_PUBLISH
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}