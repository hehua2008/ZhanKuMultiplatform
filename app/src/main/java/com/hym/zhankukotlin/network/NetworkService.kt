package com.hym.zhankukotlin.network

import com.google.gson.GsonBuilder
import com.hym.zhankukotlin.model.*
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface NetworkService {
    // https://api.zcool.com.cn/v2/api/topCate?app=android
    @GET("${Constants.API_URL}topCate")
    suspend fun getTopCate(@Query("app") app: String = "android"): TopCateResponse

    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=1&ps=10&activity=0&recommendLevel=3&contentType=0
    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=2&ps=10&activity=0&recommendLevel=3&lastId=14140227&contentType=0
    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=1&ps=10&field=33&recommendLevel=2&contentType=0
    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=2&ps=10&field=33&recommendLevel=2&lastId=14200610&contentType=0
    // https://api.zcool.com.cn/v2/api/discoverListNew?app=android&p=1&ps=10&field=33&subcate=34&recommendLevel=2&contentType=0
    @GET("${Constants.API_URL}discoverListNew")
    suspend fun getDiscoverListNew(
        @Query("app") app: String = "android",
        @Query("p") page: Int = 1,
        @Query("ps") pageSize: Int = 10,
        @Query("field") topCate: Int? = null,
        @Query("subcate") subCate: Int? = null,
        @Query("recommendLevel") recommendLevel: RecommendLevel = RecommendLevel.EDITOR_CHOICE,
        @Query("lastId") lastId: Int? = null,
        @Query("contentType") contentType: Int = 0
    ): ContentPageResponse

    // https://api.zcool.com.cn/v2/api/u/601779?app=android&p=1&ps=10&sort=8
    @GET("${Constants.API_URL}u/{uid}")
    suspend fun getUserContentList(
        @Path("uid") uid: Int,
        @Query("app") app: String = "android",
        @Query("p") page: Int = 1,
        @Query("ps") pageSize: Int = 10,
        @Query("sort") sort: SortOrder = SortOrder.LATEST_PUBLISH,
        @Query("lastId") lastId: Int? = null
    ): ContentPageResponse

    // https://api.zcool.com.cn/v2/api/getAllCategoryListContainArticle.do?app=android
    @GET("${Constants.API_URL}getAllCategoryListContainArticle.do")
    suspend fun getAllCategoryListContainArticle(@Query("app") app: String = "android"): TopCateResponse

    // https://api.zcool.com.cn/v2/api/work/ZNTY3NDkxOTI=.html?app=android
    @GET("${Constants.API_URL}work/{workId}")
    suspend fun getWorkDetails(
        @Path("workId") workId: String,
        @Query("app") app: String = "android"
    ): WorkDetailsResponse

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            val gson = GsonBuilder().registerTypeAdapterFactory(ZkTypeAdapterFactory).create()
            val client = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor())
                .addNetworkInterceptor {
                    val request = it.request()
                    println(">>>>>> $request")
                    val response = it.proceed(request)
                    println("<<<<<< $response")
                    response
                }
                //.proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 8888)))
                .build()
            val retrofit = Retrofit.Builder()
                .client(client)
                .baseUrl(Constants.API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
            val networkService = retrofit.create(NetworkService::class.java)
            runBlocking {
                val topCateResponse = networkService.getAllCategoryListContainArticle()
                topCateResponse.dataContent?.forEach {
                    println(it.name)
                    it.subCateList.forEach { sub ->
                        println("  ${sub.name}")
                    }
                }
                val discoverListNewResponse = networkService.getDiscoverListNew(
                    page = 2,
                    pageSize = 5,
                    topCate = 33
                )
                discoverListNewResponse.dataContent?.let {
                    println(it.copy(content = emptyList()))
                    it.content.forEach { c ->
                        println("    $c")
                    }
                }
                val userContentPageResponse = networkService.getUserContentList(
                    uid = 601779
                )
                userContentPageResponse.dataContent?.let {
                    println(it.copy(content = emptyList()))
                    it.content.forEach { c ->
                        println("    $c")
                    }
                }
                val workDetailsResponse = networkService.getWorkDetails("ZNTY4MTUwNjA=.html")
                workDetailsResponse.dataContent?.run {
                    println(this)
                    println(productVideosIframe)
                    println(product.fieldCateObj)
                    println(product.subCateObj)
                    println(product.productImages)
                    println(product.productTags)
                    println(product.productVideos)
                }
            }
        }
    }
}