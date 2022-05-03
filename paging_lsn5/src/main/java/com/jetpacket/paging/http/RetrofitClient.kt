package com.jetpacket.paging.http

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
object RetrofitClient {

    /**
     * https://www.wanandroid.com/project/list/1/json?cid=2
     */
    private val instance: Retrofit by lazy {
        val okhttpInterceptor = HttpLoggingInterceptor {
            Log.i("print_logs", "okhttpInterceptor:: $it")
        }
        okhttpInterceptor.level=HttpLoggingInterceptor.Level.BODY
        Retrofit.Builder()
            .client(OkHttpClient.Builder().addInterceptor(okhttpInterceptor).build())
            .baseUrl("https://www.wanandroid.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
            .build()
    }

    fun <T> createApi(clazz: Class<T>): T {
        return instance.create(clazz) as T
    }
}