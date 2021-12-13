package com.hym.zhankukotlin.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hym.zhankukotlin.model.Content
import com.hym.zhankukotlin.model.RecommendLevel
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.network.NetworkService

data class LoadParamsHolder(
    val page: Int,
    val totalPages: Int = Int.MAX_VALUE,
    val lastId: Int? = null
) {
    companion object {
        val INITIAL = LoadParamsHolder(0)
    }

    operator fun plus(other: Int): LoadParamsHolder? {
        return (page + other).let {
            if (it > totalPages) null else copy(page = it, lastId = null)
        }
    }

    operator fun minus(other: Int): LoadParamsHolder? {
        return (page - other).let {
            if (it < 1) null else copy(page = it, lastId = null)
        }
    }
}

class PreviewPagingSource(
    private val networkService: NetworkService,
    private val topCate: TopCate?,
    private val subCate: SubCate?,
    private val initialPage: Int,
    private val pageSize: Int,
    private val recommendLevel: RecommendLevel,
    private val contentType: Int
) : PagingSource<LoadParamsHolder, Content>() {
    companion object {
        private const val TAG = "PreviewPagingSource"
    }

    override suspend fun load(params: LoadParams<LoadParamsHolder>): LoadResult<LoadParamsHolder, Content> {
        try {
            val paramsKey =
                params.key ?: return LoadResult.Error(IllegalArgumentException("Empty params key!"))
            val page = if (paramsKey === LoadParamsHolder.INITIAL) initialPage else paramsKey.page
            val response = networkService.getDiscoverListNew(
                page = page,
                pageSize = pageSize,
                topCate = topCate?.id,
                subCate = subCate?.id,
                recommendLevel = recommendLevel,
                lastId = paramsKey.lastId,
                contentType = contentType
            )
            val contentPage = response.dataContent
                ?: return LoadResult.Error(IllegalArgumentException(response.msg))
            val totalPages = contentPage.totalPages
            val contentList = contentPage.content
            val nextKey = if (page >= totalPages) null
            else LoadParamsHolder(page + 1, totalPages, contentList.lastOrNull()?.id)
            return LoadResult.Page(
                data = contentList,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<LoadParamsHolder, Content>): LoadParamsHolder? {
        // Try to find the page key of the closest page to anchorPosition, from either the prevKey
        // or the nextKey, but you need to handle nullability here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so just return null.
        return state.anchorPosition?.let {
            val anchorPage = state.closestPageToPosition(it) ?: return@let null
            anchorPage.prevKey?.plus(1) ?: anchorPage.nextKey?.minus(1)
        }
    }
}