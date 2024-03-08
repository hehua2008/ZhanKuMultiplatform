package com.hym.zhankucompose.paging

import com.hym.zhankucompose.model.ContentPageResponse
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SubCate
import com.hym.zhankucompose.model.TopCate
import com.hym.zhankucompose.network.NetworkService

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