package com.ping.wanandroid.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.ToastUtils
import com.ping.wanandroid.data.Blog
import com.ping.wanandroid.net.WanService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class BlogViewModel : ViewModel() {

    private val blogs: MutableList<Blog> by lazy { mutableListOf() }

    private val _blogFlow = MutableSharedFlow<List<Blog>>()
    val blogFlow: SharedFlow<List<Blog>> = _blogFlow

    private var page = 0

    private var loading = false

    fun blog(refresh: Boolean = false) {
        if (loading) {
            return
        }
        loading = true
        viewModelScope.launch {
            if (page == 0 || refresh) {
                refresh()
            } else {
                WanService.blog(page)
                    .map {
                        it.datas!!
                    }
                    .safeFlowIO()
                    .collect {
                        blogs.addAll(it)
                        _blogFlow.emit(blogs)
                        page++
                    }
            }
        }
    }


    private suspend fun refresh() {

        val blogFlow = WanService.blog(0)
            .map {
                it.datas!!
            }.safeFlowIO()
        WanService.blogTop().zip(blogFlow) { topData, blogData ->
            blogs.clear()
            topData.forEach {
                it.isTop = true
            }
            blogs.addAll(topData)
            blogs.addAll(blogData)
        }.safeFlowIO()
            .collect {
                _blogFlow.emit(blogs)
                page = 1
            }
    }


    private fun <T> Flow<T>.safeFlowIO(): Flow<T> = flowOn(Dispatchers.IO)
        .catch {
            it.printStackTrace()
            ToastUtils.showShort(it.message)
        }.onCompletion {
            loading = false
        }

}