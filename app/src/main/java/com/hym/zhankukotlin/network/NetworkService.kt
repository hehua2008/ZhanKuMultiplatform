package com.hym.zhankukotlin.network

import retrofit2.http.GET
import retrofit2.http.Path

interface NetworkService {
    @GET(Constants.DISCOVER_URL)
    suspend fun getCategoryItemList(): List<CategoryItem>

    // https://www.zcool.com.cn/discover/33!0!0!0!0!!!!2!0!1
    @GET("{path}")
    suspend fun getPreviewResult(@Path(value = "path", encoded = true) path: String): PreviewResult

    // https://www.zcool.com.cn/work/ZNTAzNzM4Njg=.html
    @GET("{path}")
    suspend fun getDetailItem(@Path(value = "path", encoded = true) path: String): DetailItem
}