package com.jetpacket.paging.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jetpacket.paging.model.DataX
import com.jetpacket.paging.paging.ProjectListPagingSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
class ProjectListViewModel : ViewModel() {

    //防止旋转数据重新加载问题。
    // 1、ViewModel使用临时变量保存配置
    // 2、缓存起来：cachedIn(viewModelScope)
    private val projectListPager by lazy {
        Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 30,
                prefetchDistance = 1
            ), //第一次加载两页，后续加载一页
            pagingSourceFactory = { ProjectListPagingSource() }
        ).flow
            .cachedIn(viewModelScope)
            .flowOn(Dispatchers.IO)
    }

    /**
     * PagingConfig
     *  pageSize：每页显示的数据大小
     *  prefetchDistance：预刷新的距离，距离最后一个item多远时加载数据，默认是pageSize
     *  enablePlaceholders：
     *  initialLoadSize：初始化加载数量，默认是pageSize*3
     */
    fun loadList(): Flow<PagingData<DataX>> {
        Log.w("print_logs", "ProjectListViewModel::loadList")
        return projectListPager
    }

}