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

    private val _totalPages = MutableLiveData<Int>(2)
    val totalPages: LiveData<Int> = _totalPages

    private val _pageSize = MutableLiveData<Int>(25)
    val pageSize: LiveData<Int> = _pageSize

    private var sortOrder = SortOrder.LATEST_PUBLISH

    private val _mediatorLiveData = MediatorLiveData<Unit>().apply {
        addSource(page) { value = Unit }
        addSource(pageSize) { value = Unit }
    }
    val mediatorLiveData: LiveData<Unit> = _mediatorLiveData

    fun setPage(page: Int) {
        if (page < 1 || _page.value == page) return
        _page.value = page.coerceAtMost(totalPages.value ?: page)
    }

    fun setPageSize(pageSize: Int) {
        if (_pageSize.value == pageSize) return
        _pageSize.value = pageSize
    }

    fun setSortOrder(sortOrder: SortOrder) {
        if (this.sortOrder == sortOrder) return
        this.sortOrder = sortOrder
        _page.value = 1
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
                sortOrder = sortOrder
            ) {
                _totalPages.postValue(it)
            }
        }
    )
        .flow
        .cachedIn(viewModelScope)
}