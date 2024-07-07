package com.hym.zhankumultiplatform.ui.main

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankumultiplatform.di.GlobalComponent
import com.hym.zhankumultiplatform.flow.MutableSharedData
import com.hym.zhankumultiplatform.flow.SharedData
import com.hym.zhankumultiplatform.model.Content
import com.hym.zhankumultiplatform.model.RecommendLevel
import com.hym.zhankumultiplatform.model.SubCate
import com.hym.zhankumultiplatform.model.TopCate
import com.hym.zhankumultiplatform.network.NetworkService
import com.hym.zhankumultiplatform.paging.LoadParamsHolder
import com.hym.zhankumultiplatform.paging.PreviewPagingSource
import com.hym.zhankumultiplatform.paging.TotalPagesCallback
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.Flow

class PreviewPageViewModel(
    val topCate: TopCate,
    initialSubCate: SubCate?,
    private val networkService: NetworkService = GlobalComponent.Instance.networkService
) : ViewModel() {
    companion object {
        private const val TAG = "PreviewPageViewModel"
        val PageSizeList = listOf(10, 25, 50, 100, 200, 400).toImmutableList()
    }

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

    private val _subCate = MutableSharedData<SubCate?>(initialSubCate)
    val subCate: SharedData<SubCate?> = _subCate

    private var recommendLevel = RecommendLevel.EDITOR_CHOICE

    private var contentType: Int = 0

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

    fun setSubCate(subCate: SubCate?) {
        if (_subCate.value?.id == subCate?.id) return
        _subCate.value = subCate
        totalPagesCallback.invalidate()
        _page.value = 1
    }

    fun setRecommendLevel(recommendLevel: RecommendLevel) {
        if (this.recommendLevel == recommendLevel) return
        this.recommendLevel = recommendLevel
        totalPagesCallback.invalidate()
        _page.value = 1
    }

    fun setContentType(contentType: Int) {
        if (this.contentType == contentType) return
        this.contentType = contentType
        totalPagesCallback.invalidate()
        _page.value = 1
    }

    val pagingFlow: Flow<PagingData<Content>> = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        config = PagingConfig(pageSize = PageSizeList[pageSizeIndex.value]),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            PreviewPagingSource(
                networkService = networkService,
                topCate = topCate,
                subCate = subCate.value,
                initialPage = page.value,
                pageSize = PageSizeList[pageSizeIndex.value],
                recommendLevel = recommendLevel,
                contentType = contentType,
                totalPagesCallback = totalPagesCallback
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}