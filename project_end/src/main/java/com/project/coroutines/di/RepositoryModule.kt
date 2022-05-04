package com.project.coroutines.di

import com.project.coroutines.api.ApiService
import com.project.coroutines.db.AppDataBase
import com.project.coroutines.mapper.EntityToModelMapper
import com.project.coroutines.repository.Repository
import com.project.coroutines.repository.RepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.android.scopes.ViewModelScoped

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */

@InstallIn(ViewModelComponent::class) //或者：ActivityRetainedComponent::class
@Module
object RepositoryModule {

    @ViewModelScoped //或者 @ActivityRetainedScoped
    @Provides
    fun provideProjectRepository(api: ApiService, dataBase: AppDataBase): Repository {
        return RepositoryImpl(api, dataBase, EntityToModelMapper())
    }

}