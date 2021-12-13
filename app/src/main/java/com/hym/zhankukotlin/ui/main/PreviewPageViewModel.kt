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

    private val _pageSize = MutableLiveData<Int>(25)
    val pageSize: LiveData<Int> = _pageSize

    private val _subCate = MutableLiveData<SubCate?>()
    val subCate: LiveData<SubCate?> = _subCate

    private val _recommendLevel = MutableLiveData<RecommendLevel>(RecommendLevel.EDITOR_CHOICE)
    val recommendLevel: LiveData<RecommendLevel> = _recommendLevel

    private val _contentType = MutableLiveData<Int>(0)
    val contentType: LiveData<Int> = _contentType

    private val _mediatorLiveData = MediatorLiveData<Unit>().apply {
        addSource(page) { value = Unit }
        addSource(pageSize) { value = Unit }
        addSource(subCate) { value = Unit }
        addSource(recommendLevel) { value = Unit }
        addSource(contentType) { value = Unit }
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

    fun setSubCate(subCate: SubCate?) {
        if (_subCate.value?.id == subCate?.id) return
        _subCate.value = subCate
    }

    fun setRecommendLevel(recommendLevel: RecommendLevel) {
        if (_recommendLevel.value == recommendLevel) return
        _recommendLevel.value = recommendLevel
    }

    fun setContentType(contentType: Int) {
        if (_contentType.value == contentType) return
        _contentType.value = contentType
    }

    val pagingFlow: Flow<PagingData<Content>> = Pager(
        // Configure how data is loaded by passing additional properties to
        // PagingConfig, such as prefetchDistance.
        config = PagingConfig(pageSize = pageSize.value ?: 25),
        initialKey = LoadParamsHolder.INITIAL,
        pagingSourceFactory = {
            PreviewPagingSource(
                networkService = MyApplication.networkService,
                topCate = topCate,
                subCate = subCate.value,
                initialPage = page.value ?: 1,
                pageSize = pageSize.value ?: 25,
                recommendLevel = recommendLevel.value ?: RecommendLevel.EDITOR_CHOICE,
                contentType = contentType.value ?: 0
            )
        }
    )
        .flow
        .cachedIn(viewModelScope)
}