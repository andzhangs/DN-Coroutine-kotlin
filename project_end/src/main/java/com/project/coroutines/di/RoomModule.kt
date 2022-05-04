package com.project.coroutines.di

import android.app.Application
import androidx.room.Room
import com.project.coroutines.db.AppDataBase
import com.project.coroutines.db.dao.ProjectDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.internal.modules.ApplicationContextModule
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */
@InstallIn(SingletonComponent::class)
@Module(includes = [ApplicationContextModule::class])
object RoomModule {

    @Singleton
    @Provides
    fun provideAppDataBase(application: Application): AppDataBase {
        return Room.databaseBuilder(application, AppDataBase::class.java, "projectDB.db").build()
    }

    @Singleton
    @Provides
    fun provideProjectDao(appDataBase: AppDataBase): ProjectDao {
        return appDataBase.getProjectDao()
    }

}