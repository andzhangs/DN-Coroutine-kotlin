package com.flow.lsn3

import android.app.Application
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 *
 * @author zhangshuai
 * @date 2025/1/9 14:18
 * @description 自定义类描述
 */
class FlowApplication : Application() {

    companion object {

        private lateinit var instance: FlowApplication

        @JvmStatic
        fun getInstance() = instance
    }

    val stateFlow = MutableStateFlow(0)

    val sharedFlow= MutableSharedFlow<Int>(replay = 0, onBufferOverflow = BufferOverflow.SUSPEND)

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}