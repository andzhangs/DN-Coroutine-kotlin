package com.project.coroutines.repository

import androidx.paging.*
import com.project.coroutines.api.ApiService
import com.project.coroutines.mediator.ProjectMediator
import com.project.coroutines.db.AppDataBase
import com.project.coroutines.db.entity.ProjectEntity
import com.project.coroutines.mapper.Mapper
import com.project.coroutines.model.ProjectItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */
class RepositoryImpl(
    private val api: ApiService,
    private val dataBase: AppDataBase,
    private val mapper: Mapper<ProjectEntity, ProjectItemModel>
) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override fun fetchProjectList(): Flow<PagingData<ProjectItemModel>> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                prefetchDistance = 1,
                initialLoadSize = 30
            ),
            remoteMediator = ProjectMediator(api, dataBase)  //请求网络数据，放入到数据库
        ) {
            dataBase.getProjectDao().getAll()  //从数据库拿去数据
        }.flow
            .flowOn(Dispatchers.IO)
            .map { pagingData ->
                pagingData.map {
                    mapper.map(it)  //数据转换，给到UI 显示
                }
            }
    }

}