package com.dn.coroutine.retrofit

import android.util.Log
import com.dn.coroutine.retrofit.model.HpImageArchiveModel
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author zhangshuai
 * @date 2022/4/23 星期六
 * @email zhangshuai@dushu365.com
 * @description
 */

val instance: GithubApiService by lazy {
    Retrofit.Builder().client(OkHttpClient.Builder().addInterceptor {
        it.proceed(it.request()).apply {
            Log.i("print_logs", "requestCode：${code()}, ${request()}")
        }
    }.build())
        .baseUrl("https://cn.bing.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
        .build().create(GithubApiService::class.java)

}

interface GithubApiService {
    @GET("HPImageArchive.aspx")
    suspend fun getInfo(
        @Query("format") format: String,
        @Query("idx") idx: String,
        @Query("n") n: String
    ): HpImageArchiveModel
}