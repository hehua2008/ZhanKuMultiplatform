package com.hym.zhankucompose.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankucompose.model.Content
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.network.NetworkService
import com.hym.zhankucompose.paging.LoadParamsHolder
import com.hym.zhankucompose.paging.SearchContentPagingSource
import com.hym.zhankucompose.paging.TotalPagesCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class SearchContentPageViewModel @Inject constructor(private val networkService: NetworkService) :
    ViewModel() {
    companion object {
        private const val TAG = "SearchContentPageViewModel"
        val PageSizeList = listOf(10, 25, 50, 100, 200, 400).toImmutableList()
    }

    lateinit var contentType: ContentType

    private val _topCate = MutableLiveData<TopCate>(TopCate.All)
    val topCate: LiveData<TopCate> = _topCate

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

    private var word = ""

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
    fun setPageSizeIndex(pageSizeIndex: Int) {
        if (_pageSizeIndex.value == pageSizeIndex) return
        _pageSizeIndex.value = pageSizeIndex
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

    fun setTopCate(topCate: TopCate) {
        if (_topCate.value == topCate) return
        _topCate.value = topCate
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
        config = PagingConfig(pageSize = PageSizeList[pageSizeIndex.value ?: 0]),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            SearchContentPagingSource(
                networkService = networkService,
                initialPage = page.value ?: 1,
                pageSize = PageSizeList[pageSizeIndex.value ?: 0],
                word = word,
                contentType = contentType,
                topCate = topCate.value ?: TopCate.All,
                recommendLevel = recommendLevel,
                sortOrder = sortOrder,
                totalPagesCallback = totalPagesCallback
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}