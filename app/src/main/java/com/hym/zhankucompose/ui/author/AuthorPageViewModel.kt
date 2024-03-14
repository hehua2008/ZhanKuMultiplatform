package com.hym.zhankucompose.ui.author

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankucompose.model.Content
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.network.NetworkService
import com.hym.zhankucompose.paging.AuthorPagingSource
import com.hym.zhankucompose.paging.LoadParamsHolder
import com.hym.zhankucompose.paging.TotalPagesCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import kotlin.properties.Delegates

@HiltViewModel
class AuthorPageViewModel @Inject constructor(private val networkService: NetworkService) :
    ViewModel() {
    companion object {
        private const val TAG = "AuthorPageViewModel"
        val PageSizeList = listOf(10, 25, 50, 100, 200, 400).toImmutableList()
    }

    var authorUid by Delegates.notNull<Int>()

    private val _page = MutableLiveData<Int>(1)
    val page: LiveData<Int> = _page

    private val _pageSizeIndex = MutableLiveData<Int>(0)
    val pageSizeIndex: LiveData<Int> = _pageSizeIndex

    private val _totalPages = MutableLiveData<Int>(2)
    val totalPages: LiveData<Int> = _totalPages

    private val totalPagesCallback = object : TotalPagesCallback() {
        override fun onUpdate(totalPages: Int) {
            _totalPages.value = totalPages
        }
    }

    private var sortOrder = SortOrder.LATEST_PUBLISH

    private val _mediatorLiveData = MediatorLiveData<Unit>().apply {
        addSource(page) { value = Unit }
    }
    val mediatorLiveData: LiveData<Unit> = _mediatorLiveData

    fun setPage(page: Int) {
        if (page < 1 || _page.value == page) return
        _page.value = page.coerceAtMost(totalPages.value ?: page)
    }

    /**
     * Set the [pageSize] field does not take effect.
     * I have no idea why the server always considers the page size as 10 when returning a response.
     */
    fun setPageSizeIndex(pageSizeIndex: Int) {
        if (_pageSizeIndex.value == pageSizeIndex) return
        _pageSizeIndex.value = pageSizeIndex
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
        config = PagingConfig(pageSize = PageSizeList[pageSizeIndex.value ?: 0]),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            AuthorPagingSource(
                networkService = networkService,
                authorUid = authorUid,
                initialPage = page.value ?: 1,
                pageSize = PageSizeList[pageSizeIndex.value ?: 0],
                sortOrder = sortOrder,
                totalPagesCallback = totalPagesCallback
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}