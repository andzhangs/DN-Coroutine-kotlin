package com.project.coroutines.app.init

import android.content.Context
import androidx.startup.Initializer
import com.project.coroutines.app.AppHelper

/**
 * @author zhangshuai
 * @date 2022/5/5 星期四
 * @email zhangshuai@dushu365.com
 * @description  StartUp显示的设置组件初始化顺序
 * 官方文档：https://developer.android.google.cn/topic/libraries/app-startup
 */
class AppInitializer : Initializer<Unit> {

    override fun create(context: Context) {
        AppHelper.init(context)
    }
    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}