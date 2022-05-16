package com.ping.wanandroid.net

import com.ping.wanandroid.data.WanData
import kotlinx.coroutines.flow.flow

object WanService {

    private val api by lazy { ApiService.api }

    fun blog(page: Int) = flow {
        val wanData = api.blog(page)
        val blog = handlerCode(wanData)
        emit(blog)
    }

    fun blogTop() = flow {
        val wanData = api.blogTop()
        val blog = handlerCode(wanData)
        emit(blog)
    }


    private fun <T> handlerCode(wanData: WanData<T>): T {
        if (wanData.errorCode == 0) {
            return wanData.data!!
        }
        throw WanException("${wanData.errorMsg}")
    }
}

class WanException(msg: String) : Exception(msg)
