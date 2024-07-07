package com.hym.zhankumultiplatform.paging

import com.hym.zhankumultiplatform.model.ContentPageResponse
import com.hym.zhankumultiplatform.model.RecommendLevel
import com.hym.zhankumultiplatform.model.SubCate
import com.hym.zhankumultiplatform.model.TopCate
import com.hym.zhankumultiplatform.network.NetworkService

class PreviewPagingSource(
    private val networkService: NetworkService,
    private val topCate: TopCate?,
    private val subCate: SubCate?,
    private val pageSize: Int,
    private val recommendLevel: RecommendLevel,
    private val contentType: Int,
    initialPage: Int,
    totalPagesCallback: TotalPagesCallback? = null
) : ContentPagingSource(initialPage, totalPagesCallback) {

    override suspend fun getContentPageResponse(paramsKey: LoadParamsHolder): ContentPageResponse {
        return networkService.getDiscoverListNew(
            page = paramsKey.page,
            pageSize = pageSize,
            topCate = topCate?.id,
            subCate = subCate?.id,
            recommendLevel = recommendLevel,
            lastId = paramsKey.lastId,
            contentType = contentType
        )
    }
}