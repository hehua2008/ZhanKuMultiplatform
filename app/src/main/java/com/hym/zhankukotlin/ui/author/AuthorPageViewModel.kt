package com.hym.zhankukotlin.ui.author

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.model.SortOrder
import com.hym.zhankukotlin.network.NetworkService
import com.hym.zhankukotlin.paging.AuthorPagingSource
import com.hym.zhankukotlin.paging.LoadParamsHolder
import com.hym.zhankukotlin.paging.TotalPagesCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class AuthorPageViewModel @Inject constructor(private val networkService: NetworkService) :
    ViewModel() {
    companion object {
        private const val TAG = "AuthorPageViewModel"
    }

    var authorUid by Delegates.notNull<Int>()

    private val _page = MutableLiveData<Int>(1)
    val page: LiveData<Int> = _page

    private val _totalPages = MutableLiveData<Int>(2)
    val totalPages: LiveData<Int> = _totalPages

    private val totalPagesCallback = object : TotalPagesCallback() {
        override fun onUpdate(totalPages: Int) {
            _totalPages.value = totalPages
        }
    }

    private var pageSize: Int = 25

    private var sortOrder = SortOrder.LATEST_PUBLISH

    private val _mediatorLiveData = MediatorLiveData<Unit>().apply {
        addSource(page) { value = Unit }
    }
    val mediatorLiveData: LiveData<Unit> = _mediatorLiveData

    fun setPage(page: Int) {
        if (page < 1 || _page.value == page) return
        _page.value = page.coerceAtMost(totalPages.value ?: page)
    }

    fun setPageSize(pageSize: Int) {
        if (this.pageSize == pageSize) return
        this.pageSize = pageSize
        totalPagesCallback.invalidate()
        _page.value = 1
    }

    fun setSortOrder(sortOrder: SortOrder) {
        if (this.sortOrder == sortOrder) return
        this.sortOrder = sortOrder
        totalPagesCallback.invalidate()
        _page.value = 1
    }

    val pagingFlow: Flow<PagingData<Content>> = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        config = PagingConfig(pageSize = pageSize),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            AuthorPagingSource(
                networkService = networkService,
                authorUid = authorUid,
                initialPage = page.value ?: 1,
                pageSize = pageSize,
                sortOrder = sortOrder,
                totalPagesCallback = totalPagesCallback
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}