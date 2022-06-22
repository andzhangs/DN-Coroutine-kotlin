package com.project.coroutines.ext

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log

/**
 * @author zhangshuai
 * @date 2022/5/5 星期四
 * @email zhangshuai@dushu365.com
 * @description
 * 参考文章：https://www.cnblogs.com/guanxinjing/p/13178067.html
 */
fun Context.isConnectedNetwork(): Boolean = run {
    val service = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        val networks = service.allNetworks
        for (item in networks){
            service.getNetworkCapabilities(item)?.let {
                when {
                    it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        Log.i("print_logs", "Context.isConnectedNetwork: item wifi 网络")
                    }
                    it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        Log.i("print_logs", "Context.isConnectedNetwork: item wifi 移动网络")
                    }
                    else -> {

                    }
                }
            }
        }
        return service.activeNetwork != null  //如果为null表示没有网络
    }else{
        val activeNetwork: NetworkInfo? = service.activeNetworkInfo
        activeNetwork?.isConnectedOrConnecting == true
    }
}