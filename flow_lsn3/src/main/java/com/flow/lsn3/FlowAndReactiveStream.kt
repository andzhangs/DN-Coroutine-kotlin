package com.flow.lsn3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.cancellable
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.system.measureTimeMillis

/**
 * @Author zhangshuai
 * @Date 2021/9/7
 * @Emial zhangshuai@dushu365.com
 * @Description 流和响应流
 */
object FlowAndReactiveStream {

    /**
     * Flow
     *  含义：是一种类似于序列的冷流，flow构建器中的代码直到流被收集的时候才运行
     *  流的连续性：
     *      1、流的每次单独收集都是按顺序执行的，除非使用特殊操作符
     *      2、从上游到下游的每个过渡操作符都会处理每个发射出的值，然后再交给末端操作符。
     *  流的构建器：
     *      1、flowOf构建器定义了一个发射固定值集的流
     *      2、使用.asFlow()扩展函数，可以降各种集合与序列转换为流
     *  流的上下文：
     *      1、流的收集总是在调用协程的上下文发生，流的该属性称为上下文保存
     *      2、flow{..}构建器中的代码必须遵循上下文保存属性，并且不允许从其他上下文中发射
     *      3、flowOn操作符，该函数用来更改流发射的上下文
     *  启动流：
     *      1、使用launchIn替换collect我们可以在单独的协程中启动流的收集
     *  流的取消：
     *      1、流采用与协程同样的协作取消。流的收集可以是当流在一个可取消的挂起函数中挂起的时候取消
     *  流的取消检测
     *      1、流构建器对每个发射至执行附加的ensureActive检测以进行取消，意味着从flow{..}发出的繁忙循环是可以取消的
     *      2、出于性能原因，大多数其他流操作不会自行执行其他取消检测，在协程出于繁忙循环的情况下，必须明确检测是否取消
     *      3、通过cancellable操作符来执行操作
     *  背压：
     *      1、buffer()，并发运行流中发射元素的代码
     *      2、conflate()，合并发射项，不对每个值进行处理
     *      3、collectLatest()，取消并重新发射最后的一个值
     *      4、当必须更改CoroutineDispatcher,flowOn操作符使用了相同的缓冲机制，
     *         但是buffer函数显式的请求缓冲而不改变上下文
     */

    /**
     * 异步流
     */
    //表示多个值，但不是异步
    private fun simple1(): List<Int> = listOf(1, 2, 3)

    //序列,返回多个值，是同步的
    private fun simple2(): Sequence<Int> = sequence { //序列构造器
        for (i in 1..3) {
            Thread.sleep(100) //模拟计算
//            println("simple2= ${System.currentTimeMillis()}")
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
    private fun simple4() = flow {
        println("Flow started ${Thread.currentThread()}")
        for (i in 1..3) {
            delay(1000L) //模拟延时
//            Thread.sleep(100L) //Thread.sleep 代替 delay 以观察主线程在本案例中被阻塞了
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
            emit("simple4::flow: $i，$date")
        }
    }


    private fun simple5() = flow {
        println("Flow started ${Thread.currentThread()}")
        for (i in 1..3) {
            delay(1000L) //模拟延时
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
            emit("simple4::flow: $i，$date")
        }
    } //.flowOn(Dispatchers.Default)

    @JvmStatic
    fun main(args: Array<String>) {
        println("------------------集合------------------")
        simple1().forEach { print("$it ") }
        println("\n------------------序列------------------")
        simple2().forEach { print("$it ") }
        println("\n------------------挂起------------------")
        runBlocking {
            val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
            println("flow::before= $date")
            simple3().forEach { print("$it ") }
            val dat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
            println("\nflow::after= $dat2")
        }
        runBlocking {
            launch {
                for (k in 1..3) {
                    println("launch：$k")
                    delay(100L)
                }
            }
        }

        //冷流
        println("\n------------------冷流------------------")
        runBlocking {
            println("Calling simple4 function...")
            val flow = simple4()
            println("Calling collect...")
            flow.collect { println(it) }  //冷：意味着只有在调用的时候才会使用
            println("Calling collect again...")
            flow.collect { println(it) }
        }

        //流的连续性
        println("\n------------------流的连续性------------------")
        runBlocking {
            (1..5).asFlow().filter { it % 2 == 0 }.map { "输出：$it" }.collect { print("$it ") }
        }

        //流的构建器
        println("\n------------------流的构建器------------------")
        runBlocking {
            flowOf("one", "two", "three")
                .onEach { delay(1000L) }
                .collect { println("[$it , ${System.currentTimeMillis()}] ") }
        }

        //流的上下文
        println("\n------------------流的上下文------------------")
        runBlocking {
            simple5()
                .flowOn(Dispatchers.Default)//更改流发射的上下文
                .collect { println("转换后： ${Thread.currentThread()}") }
        }

        //启动流
        println("\n------------------启动流------------------")
        runBlocking {
            val job=(1..3).asFlow()
                .onEach { delay(100L) }
                .flowOn(Dispatchers.Default)
                .onEach { println("Event $it, ${Thread.currentThread()}") }
                .onCompletion { println("flow onCompletion") } //在发送数据收集完之后添加数据
                .launchIn(this) //在单独的协程中启动流的收集

            delay(400L)
            job.cancelAndJoin()
        }

        //流的取消
        println("\n------------------流的取消------------------")
        runBlocking {
            //流在超时的情况下取消并停止执行
            withTimeoutOrNull(1500L) {
                emptyFlow<Int>().onEmpty {
                    (1..3).forEach {
                        delay(1000L)
                        emit(it)
                    }
                }.collect { println(it) }
            }
            println("Done")
        }

        //流的取消检测
        println("\n------------------流的取消检测------------------")
        runBlocking {
            //方式一
            (1..5).asFlow().cancellable().collect {
                println("输出-1：$it")
                if (it == 3) cancel()
            }
        }

        //背压
        println("\n------------------背压------------------")
        runBlocking {
            val time = measureTimeMillis {
                flowOf("one", "two", "three")
                    .onEach { delay(100L) }
//                    .flowOn(Dispatchers.Default) //1054  flowOn和buffer同时打开：1059
                    .buffer(Channel.BUFFERED) //1052
//                    .conflate() //742 加速消费者消费，会有丢包问题
                    .collect { //1227m
                        delay(300L)
                        println("[$it , ${System.currentTimeMillis()}] ")
                    }
            }
            println("消费时间：$time")

            val time2 = measureTimeMillis {
                flowOf("one", "two", "three")
                    .onEach { delay(100L) }
                    .collectLatest {
                        delay(300L)
                        println("[$it , ${System.currentTimeMillis()}] ")
                    }
            }
            println("消费时间：$time2")
        }
    }
}