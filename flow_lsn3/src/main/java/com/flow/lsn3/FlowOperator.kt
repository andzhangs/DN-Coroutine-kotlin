package com.flow.lsn3

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking

/**
 * @author zhangshuai
 * @date 2022/4/19 星期二
 * @email zhangshuai@dushu365.com
 * @description 操作符
 */
object FlowOperator {

    @JvmStatic
    fun main(args: Array<String>) = runBlocking {
//        convertOperation()
//        lengthLimitOperation()
//        endOperation()
//        collectOperation()
//        exhibitionAdvection()
//        flowException()
//        flowComplete()
    }

    /**
     * 转换操作符
     * Map
     * transform
     */
    private fun convertOperation() = runBlocking {
        println("------------------转换操作符 「map,transform」 ------------------")
        //方式一
        (1..3).asFlow()
            .map {
                delay(100L)
                "response-$it "
            }
            .collect {
                print("$it ")
            }

        //方式二
        (1..3).asFlow()
            .transform {
                emit("Making request $it")
                delay(100L)
                emit("response- $it ")
            }.collect {
                println("$it ")
            }
    }

    /**
     * 限长操作符
     */
    private fun lengthLimitOperation() = runBlocking {
        println("\n------------------限长操作符 「take」 ------------------")
        val data = flow {
            emit(1)
            emit(2)
            println("截止操作")
            emit(3)
        }
        data
            .take(2)
            .collect {
                println("$it ")
            }
    }

    /**
     * 末端操作符
     */
    private fun endOperation() = runBlocking {
        println("\n------------------末端操作符 「toList、reduce、fold,onEach」 ------------------")
        //reduce 和 fold 则可将最终的值转为单一的值
        val sum = (1..5).asFlow()
            .fold(10) { a, b ->
                a + b
            }
//            .reduce { accumulator, value ->
//                accumulator + value
//            }
        println("乘积累加：$sum")
    }

    /**
     * 组合操作符
     */
    private fun collectOperation() = runBlocking {
        println("\n------------------组合操作符 「zip,combine」 ------------------")
        val flow1 = (1..3).asFlow().onEach { delay(100L) }
        val flow2 = flowOf("one", "two", "three").onEach { delay(300L) }
        val startTime = System.currentTimeMillis()
        flow1.zip(flow2) { a, b ->
            "$a -> $b"
        }.collect { println("$it ${System.currentTimeMillis() - startTime}ms from start") }

        //尽量使用zip数据更准，延时会导致combine数据丢失
        flow1.combine(flow2) { a, b ->
            "$a and $b"
        }.collect { println("$it  ${System.currentTimeMillis() - startTime}ms from start") }
    }


    /**
     * 组合操作符
     * flatMapContact： 相应序列操作符最相近的类似物,它们在等待内部流完成之前开始收集下一个值。
     * flatMapMerge： 并发收集所有传入的流，并将它们的值合并到一个单独的流，以便尽快的发射值。
     * flatMapLatest：在发出新流后立即取消先前流的收集。
     */
    @OptIn(FlowPreview::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    private fun exhibitionAdvection() = runBlocking {
        println("\n------------------展平流 「flatMapContact,flatMapMerge,flatMapLatest」 ------------------")
        fun flowMethod(i: Int) = flow<String> {
            emit("$i Start")
            delay(500L)
            emit("$i End")
        }

        val startTime = System.currentTimeMillis()

        (1..3).asFlow()
            .flatMapConcat { flowMethod(it) }
            .collect { println("$it ${System.currentTimeMillis() - startTime}ms from start") }
        println("------------------")
        (1..3).asFlow()
            .flatMapMerge { flowMethod(it) }
            .collect { println("$it ${System.currentTimeMillis() - startTime}ms from start") }
        println("------------------")
        (1..3).asFlow()
            .flatMapLatest { flowMethod(it) }
            .collect { println("$it ${System.currentTimeMillis() - startTime}ms from start") }
    }

    /**
     * 流异常
     * 收集器 try 与 catch
     * catch：保留此异常的透明性并允许封装它的异常处理
     */
    private fun flowException() = runBlocking {
        println("\n------------------流异常处理 「try{}catch{},catch」 ------------------")
        val dataFlow = flow<Int> {
            for (i in 1..3) {
                println("Emitting $i")
                emit(i)
            }
        }
        try {
            dataFlow.collect {
                check(it <= 1)
                println(it)
            }
        } catch (e: Exception) {
            println(e)
        }

        println("------------------")

        val dataFlow2 = flow {
            for (i in 'a'..'c') {
                println("Emitting $i")
                emit(i.toString())
                throw ArithmeticException("上游出现了异常")
            }
        }.catch {
            "Caught $it"
            emit("d")  //异常中恢复数据
        }.flowOn(Dispatchers.Default)
        dataFlow2.collect(::println)


    }


    /**
     * 流的完成
     */
    private fun flowComplete() = runBlocking {
        println("\n------------------流的完成 「try{}finally{}, onCompletion」 ------------------")
        val simple = flowOf(1, 2, 3).onEach { delay(100L) }

        //命令式
        try {
            simple.collect(::println)
        } finally {
            println("命令式：Done")
        }

        //声明式
        val simple2= flow {
            emit(1)
            delay(500L)
            emit(2)
            throw IndexOutOfBoundsException()
        }
        simple2.onCompletion { ex->  //来自上下游异常信息
            if (ex != null){
                println("声明式：Done 获取异常：$ex")
            }
        } //获取到异常信息
        .catch{ println("捕获异常：$it")} //捕获上游异常信息
        .collect{
//            check( it <= 2) //下游异常，需要try{}catch{}
            println(it)
        }
    }

}