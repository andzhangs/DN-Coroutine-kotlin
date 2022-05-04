package com.project.coroutines.app

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.startup.AppInitializer
import androidx.startup.InitializationProvider
import androidx.startup.Initializer
import androidx.startup.StartupLogger
import androidx.work.Configuration
import coil.ImageLoader
import coil.ImageLoaderFactory
import com.project.coroutines.R
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
@HiltAndroidApp
class ProjectApplication : Application(), ImageLoaderFactory, Configuration.Provider {

    companion object {
        lateinit var INSTANCE: ProjectApplication
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    /**
     * Return a new [ImageLoader].
     */
    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)  //淡入淡出
            .placeholder(R.mipmap.ic_launcher_round)  //占位图
            .build()
    }

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder().setWorkerFactory(workerFactory).build()
    }
}