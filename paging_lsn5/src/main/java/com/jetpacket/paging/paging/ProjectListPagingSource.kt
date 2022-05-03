package com.jetpacket.paging.paging

import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jetpacket.paging.api.ApiService
import com.jetpacket.paging.http.BaseResponse
import com.jetpacket.paging.http.RetrofitClient
import com.jetpacket.paging.model.Data
import com.jetpacket.paging.model.DataX
import kotlinx.coroutines.delay

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
class ProjectListPagingSource : PagingSource<Int, DataX>() {

    //刷新的起始页码位置
    override fun getRefreshKey(state: PagingState<Int, DataX>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, DataX> {
//        delay(1000L)
        Log.e("print_logs", "load::params.key页码= ${params.key} , loadSize页条数= ${params.loadSize}")

        val currentPage = if (params.key == 0 || params.key == -1) {
            1
        } else {
            params.key ?: 1
        }

        val cid = currentPage //params.loadSize
        val apiService = RetrofitClient.createApi(ApiService::class.java).getList(currentPage, cid)
        var prevKey: Int?
        var nextKey: Int?

        val realPageSize = 15
        val initialLoadSize = 30
        if (currentPage == 1) {
            prevKey = null
            nextKey = initialLoadSize / realPageSize //+1
        } else {
            prevKey = currentPage - 1
            nextKey = if (apiService.data?.over == false) currentPage + 1 else null
        }

        return try {
            LoadResult.Page(
                data = apiService.data?.datas as List<DataX>,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

}