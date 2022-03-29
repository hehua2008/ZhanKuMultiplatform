package com.hym.zhankukotlin.paging

import com.hym.zhankukotlin.model.ContentPageResponse
import com.hym.zhankukotlin.model.RecommendLevel
import com.hym.zhankukotlin.model.SubCate
import com.hym.zhankukotlin.model.TopCate
import com.hym.zhankukotlin.network.NetworkService

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