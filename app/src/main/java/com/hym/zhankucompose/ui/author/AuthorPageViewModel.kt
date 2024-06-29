package com.hym.zhankucompose.ui.author

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankucompose.di.GlobalComponent
import com.hym.zhankucompose.flow.MutableSharedData
import com.hym.zhankucompose.flow.SharedData
import com.hym.zhankucompose.model.Content
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.network.NetworkService
import com.hym.zhankucompose.paging.AuthorPagingSource
import com.hym.zhankucompose.paging.LoadParamsHolder
import com.hym.zhankucompose.paging.TotalPagesCallback
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import kotlin.properties.Delegates

class AuthorPageViewModel(private val networkService: NetworkService = GlobalComponent.Instance.networkService) :
    ViewModel() {
    companion object {
        private const val TAG = "AuthorPageViewModel"
        val PageSizeList = listOf(10, 25, 50, 100, 200, 400).toImmutableList()
    }

    var authorUid by Delegates.notNull<Int>()

    private val _page = MutableSharedData<Int>(1)
    val page: SharedData<Int> = _page

    private val _pageSizeIndex = MutableSharedData<Int>(0)
    val pageSizeIndex: SharedData<Int> = _pageSizeIndex

    private val _totalPages = MutableSharedData<Int>(2)
    val totalPages: SharedData<Int> = _totalPages

    private val totalPagesCallback = object : TotalPagesCallback() {
        override fun onUpdate(totalPages: Int) {
            _totalPages.value = totalPages
        }
    }

    private var sortOrder = SortOrder.LATEST_PUBLISH

    fun setPage(page: Int) {
        if (page < 1 || _page.value == page) return
        _page.value = page.coerceAtMost(totalPages.value)
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
        config = PagingConfig(pageSize = PageSizeList[pageSizeIndex.value]),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            AuthorPagingSource(
                networkService = networkService,
                authorUid = authorUid,
                initialPage = page.value,
                pageSize = PageSizeList[pageSizeIndex.value],
                sortOrder = sortOrder,
                totalPagesCallback = totalPagesCallback
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}