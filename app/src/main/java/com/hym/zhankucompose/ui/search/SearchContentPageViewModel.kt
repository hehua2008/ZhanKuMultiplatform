package com.hym.zhankucompose.ui.search

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankucompose.model.*
import com.hym.zhankucompose.network.NetworkService
import com.hym.zhankucompose.paging.LoadParamsHolder
import com.hym.zhankucompose.paging.SearchContentPagingSource
import com.hym.zhankucompose.paging.TotalPagesCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchContentPageViewModel @Inject constructor(private val networkService: NetworkService) :
    ViewModel() {
    companion object {
        private const val TAG = "SearchContentPageViewModel"
    }

    lateinit var contentType: ContentType

    private val _page = MutableLiveData<Int>(1)
    val page: LiveData<Int> = _page

    private val _totalPages = MutableLiveData<Int>(2)
    val totalPages: LiveData<Int> = _totalPages

    private val totalPagesCallback = object : TotalPagesCallback() {
        override fun onUpdate(totalPages: Int) {
            _totalPages.value = totalPages
        }
    }

    private var pageSize: Int = 10

    private var word = ""

    private var topCate: TopCate? = null

    private var recommendLevel = RecommendLevel.ALL_LEVEL

    private var sortOrder = SortOrder.BEST_MATCH

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
    fun setPageSize(pageSize: Int) {
        if (this.pageSize == pageSize) return
        this.pageSize = pageSize
        totalPagesCallback.invalidate()
        _page.value = 1
    }

    fun setWord(word: String) {
        val word = word.trim()
        if (this.word == word) return
        this.word = word
        totalPagesCallback.invalidate()
        _page.value = 1
    }

    fun setTopCate(topCate: TopCate?) {
        if (this.topCate == topCate) return
        this.topCate = topCate
        totalPagesCallback.invalidate()
        _page.value = 1
    }

    fun setRecommendLevel(recommendLevel: RecommendLevel) {
        if (this.recommendLevel == recommendLevel) return
        this.recommendLevel = recommendLevel
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
            SearchContentPagingSource(
                networkService = networkService,
                initialPage = page.value ?: 1,
                pageSize = pageSize,
                word = word,
                contentType = contentType,
                topCate = topCate,
                recommendLevel = recommendLevel,
                sortOrder = sortOrder,
                totalPagesCallback = totalPagesCallback
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}