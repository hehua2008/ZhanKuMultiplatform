package com.hym.zhankukotlin.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.hym.zhankukotlin.model.*

abstract class ContentPagingSource(
    private val initialPage: Int,
    private val totalPagesCallback: TotalPagesCallback? = null
) : PagingSource<LoadParamsHolder, Content>() {
    companion object {
        private const val TAG = "ContentPagingSource"
    }

    private var isRefreshAfterInvalidate = true

    protected abstract suspend fun getContentPageResponse(paramsKey: LoadParamsHolder): ContentPageResponse

    final override suspend fun load(params: LoadParams<LoadParamsHolder>): LoadResult<LoadParamsHolder, Content> {
        try {
            isRefreshAfterInvalidate = false
            var paramsKey =
                params.key ?: return LoadResult.Error(IllegalArgumentException("Empty params key!"))
            if (paramsKey === LoadParamsHolder.INITIAL) {
                paramsKey = totalPagesCallback?.totalPages?.let {
                    LoadParamsHolder.INITIAL.copy(page = initialPage, totalPages = it)
                } ?: getContentPageResponse(LoadParamsHolder.INITIAL.copy(page = 10000))
                    .dataContent?.run {
                        if (numberOfElements <= 0) null
                        else LoadParamsHolder.INITIAL.copy(page = initialPage, totalPages = number)
                    } ?: LoadParamsHolder.INITIAL.copy(page = initialPage)
            }
            val response = getContentPageResponse(paramsKey)
            val contentPage = response.dataContent
                ?: return LoadResult.Error(IllegalArgumentException(response.msg))
            val totalPages = paramsKey.totalPages.takeIf { it != Int.MAX_VALUE }
                ?: contentPage.totalPages
            totalPagesCallback?.setTotalPages(totalPages)
            val contentList = contentPage.content
            val nextKey = if (paramsKey.page >= totalPages || contentList.isEmpty()) null
            else LoadParamsHolder(paramsKey.page + 1, totalPages, contentList.last().id)
            return LoadResult.Page(
                data = contentList,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            return LoadResult.Error(e)
        }
    }

    final override fun getRefreshKey(state: PagingState<LoadParamsHolder, Content>): LoadParamsHolder? {
        if (isRefreshAfterInvalidate) return LoadParamsHolder.INITIAL
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