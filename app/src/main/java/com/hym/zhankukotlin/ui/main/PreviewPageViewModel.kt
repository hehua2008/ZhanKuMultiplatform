package com.hym.zhankukotlin.ui.main

import androidx.lifecycle.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.hym.zhankukotlin.MyApplication
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.model.RecommendLevel
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.paging.LoadParamsHolder
import com.hym.zhankukotlin.paging.PreviewPagingSource
import kotlinx.coroutines.flow.Flow

class PreviewPageViewModel(private val topCate: TopCate? = null) : ViewModel() {
    companion object {
        private const val TAG = "PreviewPageViewModel"
    }

    private val _page = MutableLiveData<Int>(1)
    val page: LiveData<Int> = _page

    private val _totalPages = MutableLiveData<Int>(2)
    val totalPages: LiveData<Int> = _totalPages

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
        _page.value = 1
    }

    fun setSubCate(subCate: SubCate?) {
        if (_subCate.value?.id == subCate?.id) return
        _subCate.value = subCate
        _page.value = 1
    }

    fun setRecommendLevel(recommendLevel: RecommendLevel) {
        if (this.recommendLevel == recommendLevel) return
        this.recommendLevel = recommendLevel
        _page.value = 1
    }

    fun setContentType(contentType: Int) {
        if (this.contentType == contentType) return
        this.contentType = contentType
        _page.value = 1
    }

    val pagingFlow: Flow<PagingData<Content>> = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        config = PagingConfig(pageSize = pageSize),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            PreviewPagingSource(
                networkService = MyApplication.networkService,
                topCate = topCate,
                subCate = subCate.value,
                initialPage = page.value ?: 1,
                pageSize = pageSize,
                recommendLevel = recommendLevel,
                contentType = contentType
            ) {
                _totalPages.postValue(it)
            }
        }
    )
        .flow
        .cachedIn(viewModelScope)
}