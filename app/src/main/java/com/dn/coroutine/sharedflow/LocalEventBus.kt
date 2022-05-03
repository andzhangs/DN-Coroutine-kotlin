package com.dn.coroutine.sharedflow

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * @author zhangshuai
 * @date 2022/5/2 星期一
 * @email zhangshuai@dushu365.com
 * @description
 */
object LocalEventBus {
    @JvmStatic
    private val sharedFlow = MutableSharedFlow<Event>()
    val flow = sharedFlow.asSharedFlow()

    suspend fun postEvent(event: Event) {
        sharedFlow.emit(event)
    }
}

data class Event(val timestamp: Long)