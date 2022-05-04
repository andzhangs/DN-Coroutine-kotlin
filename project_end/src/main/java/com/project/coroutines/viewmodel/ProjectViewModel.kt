package com.project.coroutines.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.project.coroutines.model.ProjectItemModel
import com.project.coroutines.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 *
 * ViewModelComponent::class 对应 @HiltViewModel
 * ActivityRetainedComponent::class 对应 @ViewModelInject。  @ViewModelInject已经过时了
 */
@HiltViewModel
class ProjectViewModel @Inject constructor(repository: Repository) : ViewModel() {

    val liveData: LiveData<PagingData<ProjectItemModel>> =
        repository.fetchProjectList()
            .cachedIn(viewModelScope)
            .asLiveData()

}