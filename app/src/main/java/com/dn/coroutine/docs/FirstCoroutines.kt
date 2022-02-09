package com.dn.coroutine.docs

import kotlinx.coroutines.*

/**
 * @Author zhangshuai
 * @Date 2021/9/5
 * @Emial zhangshuai@dushu365.com
 * @Description 基础
 */
class FirstCoroutines {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            //runBlocking 阻塞当前线程来等待。是常规函数
            launch {
                delay(1000L)
                println("2-Task from runBlocking launch")
            }
            //coroutineScope只是挂起，会释放底层线程用于其他用途。是挂起函数
            coroutineScope {
                launch {
                    doWork()
                }
                delay(100L)
                println("1-Task from coroutineScope")
            }
            println("4-Coroutine Scope is over")
        }

        private suspend fun doWork(){
            delay(2000L)
            println("3-Task from nested launch")
        }
    }
}