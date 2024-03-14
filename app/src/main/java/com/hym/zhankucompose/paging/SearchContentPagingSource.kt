package com.hym.zhankucompose.paging

import com.hym.zhankucompose.model.ContentPage
import com.hym.zhankucompose.model.ContentPageResponse
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.network.NetworkService

class SearchContentPagingSource(
    private val networkService: NetworkService,
    private val word: String,
    private val contentType: ContentType,
    private val topCate: TopCate,
    private val recommendLevel: RecommendLevel,
    private val sortOrder: SortOrder,
    private val pageSize: Int,
    initialPage: Int,
    totalPagesCallback: TotalPagesCallback? = null
) : ContentPagingSource(initialPage, totalPagesCallback) {

    override suspend fun getContentPageResponse(paramsKey: LoadParamsHolder): ContentPageResponse {
        val searchContentResponse = networkService.getSearchContent(
            page = paramsKey.page,
            pageSize = pageSize,
            topCate = topCate.id,
            recommendLevel = recommendLevel,
            sort = sortOrder,
            type = contentType,
            word = word
        )
        return ContentPageResponse(
            code = searchContentResponse.code,
            msg = searchContentResponse.msg,
            dataContent = searchContentResponse.dataContent?.let {
                ContentPage(
                    content = it.content.map { wrapper -> wrapper.content },
                    first = it.first,
                    last = it.last,
                    loadNumber = it.loadNumber,
                    number = it.number,
                    numberOfElements = it.numberOfElements,
                    pageable = it.pageable,
                    size = it.size,
                    total = it.total,
                    totalContent = it.totalContent,
                    totalElements = it.totalElements,
                    totalPages = it.totalPages
                )
            }
        )
    }
}