package com.dn.coroutine.docs

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import kotlin.time.measureTime

/**
 * @Author zhangshuai
 * @Date 2021/9/5
 * @Emial zhangshuai@dushu365.com
 * @Description 组合挂起函数
 */
class CombiningPendingFunctions {

    companion object {
        private suspend fun doSomethingUsefulOne(): Int {
            println("Exe doSomethingUsefulOne()")
            delay(1000L)
            return 13
        }

        private suspend fun doSomethingUsefulTwo(): Int {
            println("Exe doSomethingUsefulTwo()")
            delay(1000L)
            return 29
        }

        @JvmStatic
        fun main(args: Array<String>) {
//            DefaultOrder()
//            AsyncOrder()
//            LazyOrder()
//            AsyncStyleMethod()
//            StructuredConcurrencyOfAsync()
            CancelByCoroutinesHierarchicalStructure()
        }

        /**
         * 默认顺序
         */
        fun DefaultOrder() = runBlocking {
            val time = measureTimeMillis {
                val one = doSomethingUsefulOne()
                val two = doSomethingUsefulTwo()
                println("The answer in ${one.plus(two)}")
            }
            println("Completed in $time ms")
        }

        /**
         * 并发
         * 程进行并发总是显式的
         */
        fun AsyncOrder() = runBlocking {
            val time = measureTimeMillis {
                val one = async { doSomethingUsefulOne() }
                val two = async { doSomethingUsefulTwo() }
                println("The answer in ${one.await().plus(two.await())}")
            }
            println("Completed in $time ms")
        }

        /**
         * 惰性启动的 async
         * 结果通过 await 获取的时候协程才会启动
         * 在计算一个值涉及挂起函数时，这个 async(start = CoroutineStart.LAZY) 的用例用于替代标准库中的 lazy 函数
         */
        fun LazyOrder() = runBlocking {
            val time = measureTimeMillis {
                val one = async(start = CoroutineStart.LAZY) { doSomethingUsefulOne() }
                val two = async(start = CoroutineStart.LAZY) { doSomethingUsefulTwo() }
                one.start()
                two.start()
                println("The answer in ${one.await().plus(two.await())}")
            }
            println("Completed in $time ms")
        }

        /**
         * async 风格的函数
         * 不推荐该方式
         */
        //这都不是挂起函数，在被调用代码中意味着是并发(异步)执行
        private fun doSomethingUsefulAsync() = GlobalScope.async { doSomethingUsefulOne() }
        private fun doSomethingUsefulTwoAsync() = GlobalScope.async { doSomethingUsefulTwo() }
        fun AsyncStyleMethod() {
            val time = measureTimeMillis {
                //在协程外面启动异步执行
                val one = doSomethingUsefulAsync()
                val two = doSomethingUsefulTwoAsync()
                //等待结果必须调用其它的挂起或者阻塞
                //等待结果的时候，这里使用runBlocking阻塞主线程
                runBlocking {
                    println("The answer in ${one.await().plus(two.await())}")
                }
            }
            println("Completed in $time ms")
        }

        /**
         * async的结构化并发
         *
         * 这种情况下，如果在 concurrentSum 函数内部发生了错误，并且它抛出了一个异常， 所有在作用域中启动的协程都会被取消
         */
        fun StructuredConcurrencyOfAsync() {
            //方式一：
            suspend fun concurrentSum(): Int = coroutineScope {
                val one = async {
                    println("当前线程-one：${Thread.currentThread().name}")
                    doSomethingUsefulOne()
                }
                val two = async {
                    println("当前线程-two：${Thread.currentThread().name}")
                    doSomethingUsefulTwo()
                }
                one.await().plus(two.await())
            }
            val time1 = measureTimeMillis {
                runBlocking {
                    println("The answer is ${concurrentSum()}")
                }
            }
            println("Completed in $time1 ms")

            //方式二：时间短、性能好一点
            val jobOne= CoroutineScope(Dispatchers.Unconfined).async {
                println("当前线程-jobOne：${Thread.currentThread().name}")
                doSomethingUsefulOne()
            }
            val jobTwo= CoroutineScope(Dispatchers.Unconfined).async {
                println("当前线程-jobTwo：${Thread.currentThread().name}")
                doSomethingUsefulTwo()
            }
            val time2 = measureTimeMillis {
                runBlocking {
                    println("The answer is ${jobOne.await().plus(jobTwo.await())}")
                }
            }
            println("Completed in $time2 ms")
        }

        //取消始终通过协程的层次结构来进行传递
        fun CancelByCoroutinesHierarchicalStructure(){
            suspend fun failedConcurrentSum():Int= coroutineScope {
                val one=async<Int> {
                    try {
                        delay(Long.MAX_VALUE)
                        42
                    }finally {
                        println("First child was cancelled")
                    }
                }
                val two=async<Int> {
                    println("Second child throw an Exception")
                    throw ArithmeticException()
                }
                one.await().plus(two.await())
            }

            runBlocking {
                try {
                    failedConcurrentSum()
                }catch (e:ArithmeticException){
                    println("Computation failed with ArithmeticException")
                }
            }
        }

    }
}