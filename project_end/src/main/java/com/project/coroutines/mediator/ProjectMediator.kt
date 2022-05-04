package com.project.coroutines.mediator

import android.util.Log
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.project.coroutines.api.ApiService
import com.project.coroutines.app.AppHelper
import com.project.coroutines.app.base.BaseResponse
import com.project.coroutines.db.AppDataBase
import com.project.coroutines.db.entity.ProjectEntity
import com.project.coroutines.ext.isConnectedNetwork
import com.project.coroutines.model.Data
import com.project.coroutines.model.ProjectItemModel

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */
@OptIn(ExperimentalPagingApi::class)
class ProjectMediator(
    private val api: ApiService,
    private val dataBase: AppDataBase
) : RemoteMediator<Int, ProjectEntity>() {

    /**
     * 处理以下事情
     * 第一步：判断LoadType
     * 第二步：请求网络分页
     * 第三步：插入到数据库
     *
     * PagingState:
     *  1、pages：List<Page<key,Value>>返回上一页的数据，主要是用来获取上一页最后一条数据作为下一页的开始位置
     *  2、config：PagingConfig返回的初始化设置的PagingConfig包含了pageSize,prefetchDistance,initialLoadSize等等
     *
     * MediatorResult：
     *  1、请求出现错误，返回MediatorResult.Error()
     *  2、请求成功且有数据，返回MediatorResult.Success(endOfPaginationReached=true)
     *  3、请求成功但没有数据，返回MediatorResult.Success(endOfPaginationReached=false)
     */
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProjectEntity>
    ): MediatorResult {
        val cid = 1
        return try {
            Log.d("print_logs", "ProjectMediator::load:当前状态= $loadType")

            //第一步：判断LoadType，根据LoadType计算 当前页-currentPage
            val pageKey = when (loadType) {
                LoadType.REFRESH -> { //在初始化刷新使用，首次访问或者调用PagingDataAdapter.refresh()触发
                    null
                }
                LoadType.PREPEND -> { //在当前列表头部添加数据的时候使用
                    return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> { //在加载更多的时候使用，需要注意的是当LoadType.REFRESH触发了，LoadType.PREPEND也会触发
                    val lastItem: ProjectEntity =
                        state.lastItemOrNull() ?: return MediatorResult.Success(
                            endOfPaginationReached = true
                        )
                    lastItem.page //返回当前内容的page字段
                }
                else -> {
                    return MediatorResult.Success(endOfPaginationReached = false)
                }
            }

            //无网络,加载本地数据
            if (!AppHelper.mContext.isConnectedNetwork()){
                return MediatorResult.Success(endOfPaginationReached = true)
            }


            //第二步：请求网络分页数据
            val currentPage =  pageKey ?: 1

            Log.i("print_logs", "ProjectMediator::load:当前页： $currentPage, page= ${state.config.pageSize}")

            //currentPage*state.config.pageSize
            val result: BaseResponse<Data> = api.getList(page = (currentPage+1), cid = cid)

            val item=result.data?.datas?.map {
                ProjectEntity(id = it.id, author = it.author, envelopePic = it.envelopePic,page = currentPage+1)
            }

            //插入到数据库
            val localState = result.data != null && result.data?.datas != null && result.data?.datas?.isNotEmpty() == true
            val dao=dataBase.getProjectDao()
            when (loadType) {
                LoadType.REFRESH -> { //在初始化刷新使用，首次访问或者调用PagingDataAdapter.refresh()触发
                    item?.let { dao.refresh(it) }
                } LoadType.APPEND -> {
                    item?.let {dao.insert(it)}
                }
                else -> {
                }
            }

            MediatorResult.Success(endOfPaginationReached = localState)
        } catch (e: Exception) {
            e.printStackTrace()
            MediatorResult.Error(e)
        }
    }
}