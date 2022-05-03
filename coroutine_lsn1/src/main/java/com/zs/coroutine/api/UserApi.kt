package com.zs.coroutine.api

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.zs.coroutine.api.data.UserDataModel
import io.reactivex.Observable
import kotlinx.coroutines.Deferred
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
//import retrofit2.converter.protobuf.ProtoConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.converter.wire.WireConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author zhangshuai
 * @date 2022/4/16 星期六
 * @email zhangshuai@dushu365.com
 * @description
 */

val userServiceApi: UserServiceApi by lazy {
    val retrofit = Retrofit.Builder()
        .client(OkHttpClient.Builder().addInterceptor {
            it.proceed(it.request()).apply {
                Log.i("print_logs", "request：$code")
            }
        }.build())
        .baseUrl("https://api.github.com/")
//        .addConverterFactory(MoshiConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .addConverterFactory(SimpleXmlConverterFactory.create())
//        .addConverterFactory(ProtoConverterFactory.create())
        .addConverterFactory(WireConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
        .build()
    retrofit.create(UserServiceApi::class.java)
}

interface UserServiceApi {

    companion object {
        @JvmStatic
        val USER_NAME = "TooCareAboutYOU"
    }

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("users/{username}")
    fun getUserInfo(@Path("username") username: String): Observable<UserDataModel>

    @Headers("Accept: application/vnd.github.v3+json")
    @GET("users/{username}")
    suspend fun getUserDataModel(@Path("username") username: String): UserDataModel

    /**
     * CoroutineCallAdapterFactory的使用场景
     */
    @Headers("Accept: application/vnd.github.v3+json")
    @GET("users/{username}")
    fun getUserDataMode2(@Path("username") username: String): Deferred<UserDataModel>

}