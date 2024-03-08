package com.hym.zhankucompose.paging

import com.hym.zhankucompose.model.ContentPageResponse
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.network.NetworkService

class AuthorPagingSource(
    private val networkService: NetworkService,
    private val authorUid: Int,
    private val pageSize: Int,
    private val sortOrder: SortOrder,
    initialPage: Int,
    totalPagesCallback: TotalPagesCallback? = null
) : ContentPagingSource(initialPage, totalPagesCallback) {

    override suspend fun getContentPageResponse(paramsKey: LoadParamsHolder): ContentPageResponse {
        return networkService.getUserContentList(
            uid = authorUid,
            page = paramsKey.page,
            pageSize = pageSize,
            sort = sortOrder,
            lastId = paramsKey.lastId
        )
    }
}