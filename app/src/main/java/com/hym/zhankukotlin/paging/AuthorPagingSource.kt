package com.hym.zhankukotlin.paging

import com.hym.zhankukotlin.model.ContentPageResponse
import com.hym.zhankukotlin.model.SortOrder
import com.hym.zhankukotlin.network.NetworkService

class AuthorPagingSource(
    private val networkService: NetworkService,
    private val authorUid: Int,
    private val pageSize: Int,
    private val sortOrder: SortOrder,
    initialPage: Int
) : ContentPagingSource(initialPage) {

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