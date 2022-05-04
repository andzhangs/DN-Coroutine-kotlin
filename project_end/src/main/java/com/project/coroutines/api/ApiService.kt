package com.project.coroutines.api

import com.project.coroutines.app.base.BaseResponse
import com.project.coroutines.model.Data
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query


/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
interface ApiService {
    @GET("project/list/{page}/json")
    suspend fun getList(@Path("page") page: Int, @Query("cid") cid: Int): BaseResponse<Data>
}