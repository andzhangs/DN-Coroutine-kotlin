package com.dn.coroutine.docs

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.forEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * @Author zhangshuai
 * @Date 2021/9/7
 * @Emial zhangshuai@dushu365.com
 * @Description 流和响应流
 */
object FlowAndReactiveStream {

    @JvmStatic
    fun main(args: Array<String>) {
        simple1().forEach { println("1-打印：$it") }
        simple2().forEach { println("2-打印：$it") }
        runBlocking { simple3().forEach { println("3-打印：$it") } }
        runBlocking {
            launch {
                for (k in 1..3){
                    println("launch：$k")
                    delay(100L)
                }
            }
            simple4().collect { println(it)  }
        }

        //冷流
        runBlocking {
            println("Calling simple4 function...")
            val flow=simple4()
            println("Calling collect...")
            flow.collect { println(it) }
            println("Calling collect again...")
            flow.collect { println(it) }
        }
    }

    /**
     *                                      异步流
     * ---------------------------------------------------------------------------------------------
     *
     * ---------------------------------------------------------------------------------------------
     */
    //表示多个值
    private fun simple1(): List<Int> = listOf(1, 2, 3)

    //序列
    private fun simple2(): Sequence<Int> = sequence { //序列构造器
        for (i in 1..3) {
            Thread.sleep(100) //模拟计算
            yield(i)
            //relay 是挂起协程并经过执行时间恢复协程，当线程空闲时就会运行协程
            //yield 是挂起协程，让协程放弃本次 cpu 执行机会让给别的协程，当线程空闲时再次运行协程。
        }
    }

    //挂起函数
    private suspend fun simple3(): List<Int> {
        delay(1000L) //模拟做了一些异步的事情
        return listOf(1, 2, 3)
    }

    //流, 异步计算的值流
    private fun simple4(): Flow<String> = flow {
        println("Flow started")
        for (i in 1..3){
            delay(100L) //模拟延时
//            Thread.sleep(100L) //Thread.sleep 代替 delay 以观察主线程在本案例中被阻塞了
            emit("flow: $i")
        }
    }

}