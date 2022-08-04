package com.hym.zhankukotlin.ui.main

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.model.RecommendLevel
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.network.NetworkService
import com.hym.zhankukotlin.paging.LoadParamsHolder
import com.hym.zhankukotlin.paging.PreviewPagingSource
import com.hym.zhankukotlin.paging.TotalPagesCallback
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@HiltViewModel
class PreviewPageViewModel @Inject constructor(private val networkService: NetworkService) :
    ViewModel() {
    companion object {
        private const val TAG = "PreviewPageViewModel"
    }

    var topCate: TopCate? = null

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

    private val _subCate = MutableLiveData<SubCate?>()
    val subCate: LiveData<SubCate?> = _subCate

    private var recommendLevel = RecommendLevel.EDITOR_CHOICE

    private var contentType: Int = 0

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
        config = PagingConfig(pageSize = pageSize),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            PreviewPagingSource(
                networkService = networkService,
                topCate = topCate,
                subCate = subCate.value,
                initialPage = page.value ?: 1,
                pageSize = pageSize,
                recommendLevel = recommendLevel,
                contentType = contentType,
                totalPagesCallback = totalPagesCallback
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}