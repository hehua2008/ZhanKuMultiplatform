package com.hym.zhankucompose.network

import com.hym.zhankucompose.model.ArticleDetailsResponse
import com.hym.zhankucompose.model.ContentPageResponse
import com.hym.zhankucompose.model.ContentType
import com.hym.zhankucompose.model.RecommendLevel
import com.hym.zhankucompose.model.SearchContentResponse
import com.hym.zhankucompose.model.SearchDesignerResponse
import com.hym.zhankucompose.model.SortOrder
import com.hym.zhankucompose.model.TopCateResponse
import com.hym.zhankucompose.model.WorkDetailsResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path

class NetworkService(private val httpClient: HttpClient) {
    // https://api.zcool.com.cn/v2/api/topCate?app=android
    suspend fun getTopCate(app: String = "android"): TopCateResponse {
        return httpClient.get {
            url {
                host = NetworkConstants.API_HOST
                path("v2/api/topCate")
                parameters.append("app", app)
            }
        }.use { response ->
            response.body()
        }
    }

    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=1&ps=10&activity=0&recommendLevel=3&contentType=0
    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=2&ps=10&activity=0&recommendLevel=3&lastId=14140227&contentType=0
    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=1&ps=10&field=33&recommendLevel=2&contentType=0
    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=2&ps=10&field=33&recommendLevel=2&lastId=14200610&contentType=0
    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=1&ps=10&field=33&subcate=34&recommendLevel=2&contentType=0
    suspend fun getDiscoverListNew(
        app: String = "android",
        page: Int = 1,
        pageSize: Int = 10,
        topCate: Int? = null,
        subCate: Int? = null,
        recommendLevel: RecommendLevel = RecommendLevel.EDITOR_CHOICE,
        lastId: Int? = null,
        contentType: Int = 0
    ): ContentPageResponse {
        return httpClient.get {
            url {
                host = NetworkConstants.API_HOST
                path("v2/api/discoverListNew")
                parameters.append("app", app)
                parameters.append("p", "$page")
                parameters.append("ps", "$pageSize")
                topCate?.let { parameters.append("field", "$it") }
                subCate?.let { parameters.append("subcate", "$it") }
                parameters.append("recommendLevel", "$recommendLevel")
                lastId?.let { parameters.append("lastId", "$it") }
                parameters.append("contentType", "$contentType")
            }
        }.use { response ->
            response.body()
        }
    }

    // https://api.zcool.com.cn/v2/api/u/601779?app=android&p=1&ps=10&sort=8
    suspend fun getUserContentList(
        uid: Int,
        app: String = "android",
        page: Int = 1,
        pageSize: Int = 10,
        sort: SortOrder = SortOrder.LATEST_PUBLISH,
        lastId: Int? = null
    ): ContentPageResponse {
        return httpClient.get {
            url {
                host = NetworkConstants.API_HOST
                path("v2/api/u/$uid")
                parameters.append("app", app)
                parameters.append("p", "$page")
                parameters.append("ps", "$pageSize")
                parameters.append("sort", "$sort")
                lastId?.let { parameters.append("lastId", "$it") }
            }
        }.use { response ->
            response.body()
        }
    }

    // https://api.zcool.com.cn/v2/api/getAllCategoryListContainArticle.do?app=android
    suspend fun getAllCategoryListContainArticle(app: String = "android"): TopCateResponse {
        return httpClient.get {
            url {
                host = NetworkConstants.API_HOST
                path("v2/api/getAllCategoryListContainArticle.do")
                parameters.append("app", app)
            }
        }.use { response ->
            response.body()
        }
    }

    // https://api.zcool.com.cn/v2/api/work/ZNTY3NDkxOTI=.html?app=android
    suspend fun getWorkDetails(
        workId: String,
        app: String = "android"
    ): WorkDetailsResponse {
        return httpClient.get {
            url {
                host = NetworkConstants.API_HOST
                path("v2/api/work/$workId")
                parameters.append("app", app)
            }
        }.use { response ->
            response.body()
        }
    }

    // https://api.zcool.com.cn/v2/api/article/ZMTQ0MDY4NA==.html?app=android
    suspend fun getArticleDetails(
        articleId: String,
        app: String = "android"
    ): ArticleDetailsResponse {
        return httpClient.get {
            url {
                host = NetworkConstants.API_HOST
                path("v2/api/article/$articleId")
                parameters.append("app", app)
            }
        }.use { response ->
            response.body()
        }
    }

    //https://api.zcool.com.cn/v2/api/search/contentList?app=android&p=1&ps=10&type=3&word=
    //https://api.zcool.com.cn/v2/api/search/contentList?app=android&p=1&ps=10&type=8&word=
    //https://api.zcool.com.cn/v2/api/search/contentList?app=android&p=1&ps=10&field=0&recommendLevel=0&sort=5&type=3&word=
    //https://api.zcool.com.cn/v2/api/search/contentList?app=android&p=1&ps=10&field=0&recommendLevel=0&sort=5&type=8&word=
    suspend fun getSearchContent(
        app: String = "android",
        page: Int = 1,
        pageSize: Int = 10,
        topCate: Int? = null,
        recommendLevel: RecommendLevel = RecommendLevel.ALL_LEVEL,
        sort: SortOrder = SortOrder.BEST_MATCH,
        type: ContentType = ContentType.WORK,
        word: String
    ): SearchContentResponse {
        return httpClient.get {
            url {
                host = NetworkConstants.API_HOST
                path("v2/api/search/contentList")
                parameters.append("app", app)
                parameters.append("p", "$page")
                parameters.append("ps", "$pageSize")
                topCate?.let { parameters.append("field", "$it") }
                parameters.append("recommendLevel", "$recommendLevel")
                parameters.append("sort", "$sort")
                parameters.append("type", "$type")
                parameters.append("word", word)
            }
        }.use { response ->
            response.body()
        }
    }

    //https://api.zcool.com.cn/v2/api/search/designer/v3?app=android&p=1&ps=10&word=
    suspend fun getSearchDesigner(
        app: String = "android",
        page: Int = 1,
        pageSize: Int = 10,
        word: String
    ): SearchDesignerResponse {
        return httpClient.get {
            url {
                host = NetworkConstants.API_HOST
                path("v2/api/search/designer/v3")
                parameters.append("app", app)
                parameters.append("p", "$page")
                parameters.append("ps", "$pageSize")
                parameters.append("word", word)
            }
        }.use { response ->
            response.body()
        }
    }
}
