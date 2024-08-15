package com.dn.coroutine.stateflow

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * @author zhangshuai
 * @date 2022/4/24 星期日
 * @email zhangshuai@dushu365.com
 * @description
 */
class StateFlowViewModel : ViewModel() {

    init {
        Log.i("print_logs", "StateFlowViewModel::")
    }


    /**
     * StateFlow是一个状态容器式可观察数据流，可以向其收集器发出当前状态更新，和新状态更新。
     * 还可以通过其value属性读取当前状态值
     */
    val number = MutableStateFlow(0)

    fun increment() {
        number.value++
    }

    fun decrement() {
        when (number.value > 0) {
            true -> {
                number.value--
            }
            else -> {
                number.value = 0
            }
        }
    }
}