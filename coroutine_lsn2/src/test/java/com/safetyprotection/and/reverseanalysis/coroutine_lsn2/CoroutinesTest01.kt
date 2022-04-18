package com.safetyprotection.and.reverseanalysis.coroutine_lsn2

import android.util.Log
import kotlinx.coroutines.*
import org.junit.Test

/**
 * @author zhangshuai
 * @date 2022/4/17 星期日
 * @email zhangshuai@dushu365.com
 * @description
 */
class CoroutinesTest01 {


    /**
     * 把主线程包装成主协程：runBlocking
     *
     * 函数命名尽量表达出干的事情，可用
     */
    @Test
    fun `test coroutines builder`() = runBlocking {
        val job=launch {
            delay(200L)
            Log.i("print_logs", "CoroutinesTest01::test coroutines builder: launch")
        }

        val deferred=async {
            delay(200L)
            Log.i("print_logs", "CoroutinesTest01::test coroutines builder: async")
        }
        deferred.await()
    }

}