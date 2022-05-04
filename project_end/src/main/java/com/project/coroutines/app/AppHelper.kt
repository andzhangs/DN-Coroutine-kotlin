package com.project.coroutines.app

import android.annotation.SuppressLint
import android.content.Context

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */

/**
 * 域名：https://www.wanandroid.com/
 * 接口：project/list/1/json?cid=2
 */
const val BaseUrl = "https://www.wanandroid.com/"

object AppHelper {

    lateinit var mContext: Context

    fun init(context: Context) {
        mContext = context
    }

}