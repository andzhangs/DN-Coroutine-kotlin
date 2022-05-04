package com.project.coroutines.repository

import androidx.paging.PagingData
import com.project.coroutines.model.ProjectItemModel
import kotlinx.coroutines.flow.Flow

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */
interface Repository {
    fun fetchProjectList(): Flow<PagingData<ProjectItemModel>>
}