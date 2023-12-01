package com.dn.coroutine.sharedflow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * @author zhangshuai
 * @date 2022/4/26 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
class SharedFlowViewModel : ViewModel() {

    /**
     * SharedFlow会向从其中收集值的所有使用方发出数据
     * SharedFlow与StateFlow不同的点，
     * 1、SharedFlow初始化没有默认值，且会保留历史值
     * 2、StateFlow有默认值，且只保留最新的一个值
     */


    private lateinit var job: Job

    fun start() {
        job = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                LocalEventBus.postEvent(Event(System.currentTimeMillis()))
            }
        }
    }

    fun stop() {
        job.cancel()
    }
}