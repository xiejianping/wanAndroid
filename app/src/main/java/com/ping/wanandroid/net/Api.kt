package com.ping.wanandroid.net

import com.ping.wanandroid.data.Blog
import com.ping.wanandroid.data.BlogData
import com.ping.wanandroid.data.WanData
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    @GET("/article/list/{page}/json")
    suspend fun blog(@Path("page") page: Int):WanData<BlogData>

    @GET("/article/top/json")
    suspend fun blogTop():WanData<List<Blog>>

}