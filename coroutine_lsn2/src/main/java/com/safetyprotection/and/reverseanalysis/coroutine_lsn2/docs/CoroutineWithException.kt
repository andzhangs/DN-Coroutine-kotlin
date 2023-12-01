package com.safetyprotection.and.reverseanalysis.coroutine_lsn2.docs

import kotlinx.coroutines.*
import java.io.IOException

/**
 * @author zhangshuai
 * @date 2022/4/17 星期日
 * @email zhangshuai@dushu365.com
 * @description 协程异常处理
 */
class CoroutineWithException {

    /**
     * 协程构建器的两种形式
     * 1、自动传播异常(launch/actor)；
     * 2、向用户暴露异常(async与produce)；
     *  当这些构建器用于创建一个"根协程"时(该协程不是另一个协程的子协程）
     *  前者这类构造器，异常会在它发生的第一时间被抛出；
     *  而后者则依赖用户来最终消费异常，例如通过await或receive
     *
     * 异常的传播特性：
     *  当一个协程由于一个异常而运行失败时，它会传播这个异常并传递个它的父级。接下来父级会进行下面几个操作：
     *   a、取消它自己的子级
     *   b、取消它自己
     *   c、将异常传播并传递给它的父级
     *
     */
    companion object {

        /**
         * 全局异常处理
         *  全局异常处理器可以获得到所有协程未处理的未捕获异常，不过它并不能对异常进行捕获，
         *  虽然不能阻止程序崩溃，全局异常处理器在程序调试和异常上报等场景中仍然有非常大的用处。
         *
         *  我们需要在classpath下面创建META-INF/services目录，
         *  并在其中创建一个名为kotlinx.coroutines.CoroutineExceptionHandler的文件，
         *  文件内容就是我们的全局异常处理器的全类名
         *
         */
        @JvmStatic
        fun globalException(){
            //全局异常的处理见
            GlobalScope.launch {
                throw RuntimeException("在全局异常处理中汇总")
            }
        }

        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
//            ExceptionPropagation()
//            ExceptionPropagation2()
//            ExceptionSupervisorJob()
//            ExceptionHandler()
            ExceptionAggregation()
        }

        private suspend fun CoroutineScope.ExceptionPropagation() {
            val job = GlobalScope.launch {
                try {
                    throw IndexOutOfBoundsException()
                } catch (e: Exception) {
                    println("Caught IndexOutOfBoundsException")
                }
            }
            job.join()

            val deferred = GlobalScope.async {
                throw ArithmeticException()
            }
            try {
                deferred.await() //若没有调用消费，async异常是不会发生
            } catch (e: Exception) {
                println("Caught ArithmeticException")
            }
        }

        /**
         * 3、非根协程的异常：
         *  其他协程所创建的协程中，产生的异常总是会被传播
         */
        private suspend fun CoroutineScope.ExceptionPropagation2() {
            val scope = CoroutineScope(Job())
            val job = scope.launch {
                async {
                    throw IllegalArgumentException()
                    //如果async抛出异常，launch就会立即抛出异常，而不会调用.await()
                }
            }
            job.join()
        }

        /**
         * SupervisorJob
         * 使用SupervisorJob时，一个子协程的运行失败不会影响到其他子协程。
         * SupervisorJob不会传播异常给它的父级。它会让子协程子级处理异常。
         *
         * 这种需求常见于在作用域内定义作业的UI组件，如果任何一个UI的子作业执行失败了，
         * 它并不总是有必要取消整个UI组件，但是如果UI组件被销毁了,由于它的结果不再被需要了,
         * 它就有必要使所有的子作业执行是被。
         *
         */
        private suspend fun CoroutineScope.ExceptionSupervisorJob() {
            //方式一
//            val supervisor = CoroutineScope(SupervisorJob())
//            val job1 = supervisor.launch {
//                delay(100L)
//                println("child 1")
//                throw IllegalArgumentException()
//            }
//            val job2 = supervisor.launch {
//                try {
//                    delay(Long.MAX_VALUE)
//                }finally {
//                    println("child2 finished")
//                }
//            }
//            delay(200L)
//            supervisor.cancel()
//            joinAll(job1,job2)

            //方式二
//            supervisorScope {
//                launch {
//                    delay(100L)
//                    println("child 1")
//                    throw IllegalArgumentException()
//                }
//
//                try {
//                    delay(Long.MAX_VALUE)
//                }finally {
//                    println("child2 finished")
//                }
//            }

            //当作业自身执行失败的时候，所有的子作业将会被全部取消
            supervisorScope {
                val child=launch {
                    try {
                        println("The child is sleeping")
                        delay(Long.MAX_VALUE)
                    }finally {
                        println("The child is cancelled")
                    }
                }
                yield() //让出执行权
                println("Throwing an exception from the scope")
                throw  AssertionError()
            }
        }

        /**
         * 异常的捕获
         *  使用CoroutineExceptionHandler对协程的异常进行捕获
         *  选满足以下条件：
         *      时机：异常是被自动抛出异常的协程所抛出的(使用launch，而不是async)
         *      位置：在CoroutineScope的CoroutineContext中,
         *           或在一个根协程(CoroutineScope或者supervisorScope的直接子协程)中
         */
        private suspend fun CoroutineScope.ExceptionHandler(){
            val handlerException= CoroutineExceptionHandler { _, throwable ->
                println("输出异常：$throwable")
            }
            //方式一：
//            val jobs= GlobalScope.launch(handlerException) {
//                throw AssertionError()
//            }
//            val deferred= GlobalScope.async(handlerException) {
//                throw ArithmeticException()
//            }
//            jobs.join()
//            deferred.await() //对他加上try{}catch{}


            //方式二：
            val scope= CoroutineScope(Job())
            val child1=scope.launch(handlerException) {
                launch {
                    throw IllegalArgumentException("child1 handlerException")
                }
            }
            child1.join()

            //方式三：
            val scope2= CoroutineScope(Job())
            val child2=scope2.launch {
                launch(handlerException) {
                    throw IllegalArgumentException("child2 handlerException")
                }
            }
            child2.join()
        }


        /**
         * 异常的聚合
         * 当协程的多个子协程因为异常而失败时，一般情况下取第一个异常进行处理。
         * 在第一个异常之后发生的所有异常，都将被绑定到第一个异常之上
         */
        private suspend fun CoroutineScope.ExceptionAggregation(){
            val handlerException= CoroutineExceptionHandler { _, throwable ->
                //取多个异常从suppressed中提取
                println("输出：第一异常: $throwable, 第二个异常：${throwable.suppressed.contentToString()}")
            }
            val parentJob=GlobalScope.launch(handlerException) {
                launch {
                    try {
                        delay(Long.MAX_VALUE)
                    }finally {
                        throw ArithmeticException()
                    }
                }
                launch {
                    try {
                        delay(100L)
                    }finally {
                        throw IOException()
                    }
                }
                launch {
                    try {
                        delay(10L)
                    }finally {
                        throw IllegalArgumentException()
                    }
                }
            }

            parentJob.join()
        }


    }
}