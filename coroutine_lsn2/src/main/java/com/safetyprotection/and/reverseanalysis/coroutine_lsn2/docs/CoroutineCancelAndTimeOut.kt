package com.safetyprotection.and.reverseanalysis.coroutine_lsn2.docs

import android.util.Log
import kotlinx.coroutines.*

/**
 * @Author zhangshuai
 * @Date 2021/9/5
 * @Emial zhangshuai@dushu365.com
 * @Description 取消和超时
 */
class CoroutineCancelAndTimeOut {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            CancelCoroutinesExe()
//            CancelIsCollaborative()
//            CancelableCalculationCode()
//            ReleaseSourceInFinally()
//            RunNonCancellableCodeBlock()
//            WithTimeout()
//            CancelAndException()
            CancelAndException2()
        }

        /**
         * 取消协程的执行
         * 在一个长时间运行的应用程序中，你也许需要对你的后台协程进行细粒度的控制
         */
        fun CancelCoroutinesExe() = runBlocking {
            val job = launch {
                repeat(1000) {
                    println("job: I'm sleeping $it ...")
                    delay(500L)
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            //取消该作业
            job.cancel()
            //等待作业的执行结束
            job.join()
            //等同于 job.cancelAndJoin()
            println("main: Now I can quit.")


            //协程抛出一个特殊的异常CancellationException来处理取消操作
            val job1 = GlobalScope.launch {
                try {
                    delay(1000L)
                    println("job-1")
                } catch (e: Exception) {
                    e.printStackTrace()
                    println("打印异常：$e")
                }
            }
            delay(500L)
            job1.cancel(CancellationException("异常抛出"))
            job1.join()


        }

        /**
         * 协程是协作的
         * CPU密集型任务取消
         *  isActive是一个可以被使用在CoroutineScope中的扩展属性，检查Job是否处于活跃状态
         *  ensureActive()：如果Job处于非活跃状态，这个方法回立即抛出异常
         *  yield()函数会检查所在协程的状态，如果已经取消，则会抛出CancellationException予以响应。
         *         此外，它还会尝试让出线程的执行权，给其他协程提供执行的机会，当线程空闲时再次运行协程。
         */
        fun CancelIsCollaborative() = runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (i < 5) { //一个执行计算的循环，只是为了占用CPU
                    //ensureActive()
                    //每秒打印消息两次
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin()
            println("main: Now I can quit.")
        }

        /**
         * 使计算代码可以取消
         *
         */
        fun CancelableCalculationCode() = runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (isActive) { //一个执行计算的循环，只是为了占用CPU
                    //每秒打印消息两次
                    if (System.currentTimeMillis() >= nextPrintTime) {
                        println("job: I'm sleeping ${i++} ...")
                        nextPrintTime += 500L
                    }
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin()
            println("main: Now I can quit.")
        }

        /**
         * finally中释放资源
         */
        fun ReleaseSourceInFinally() = runBlocking {
            val job = launch {
                try {
                    repeat(1000) {
                        println("job: I'm sleeping $it")
                        delay(500L)
                    }
                } finally {
                    withContext(NonCancellable) {
                        println("job：I'm running finally")
                    }
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // 取消该作业并等待它结束
            println("main: Now I can quit.")
        }

        /**
         * 处于取消中状态的协程不能够挂起(运行不能取消的代码块)，当协程被取消后需要调用挂起函数。
         * 我们需要将清理任务的代码放置于 NonCancellable CoroutineContext中
         *
         */
        fun RunNonCancellableCodeBlock() = runBlocking {
            val job = launch {
                try {
                    repeat(1000) {
                        println("job: I'm sleeping $it")
                        delay(500L)
                    }
                } finally {
                    //delay(1000L)
                    withContext(NonCancellable) {
                        println("job：I'm running finally")
                        delay(1000L)
                        println("job: And I've just delayed for 1 sec because I'm non-cancellable")
                    }
                }
            }
            delay(1300L)
            println("main: I'm tired of waiting!")
            job.cancelAndJoin() // 取消该作业并等待它结束
            println("main: Now I can quit.")
        }

        /**
         * 超时
         */
        fun WithTimeout() = runBlocking {
            val result = withTimeoutOrNull(1300L) {
                repeat(1000) {
                    println("job: I'm sleeping $it")
                    delay(500L)
                }
                //在它运行得到结果之前取消它
                "Done"
            } ?: "Not Done"
            println("Result is $result")


            //ithTimeout 抛出了 TimeoutCancellationException，它是 CancellationException 的子类
//            withTimeout(1300L){
//                repeat(1000){
//                    println("job: I'm sleeping $it")
//                    delay(500L)
//                }
//            }
        }

        /**
         * 取消与异常
         * 1、取消与异常紧密相关，协程内部使用CancellationException来进行取消，这个异常会被忽略
         * 2、当子协程被取消时，不会取消它的父协程
         * 3、如果一个协程遇到了CancellationException以外的异常，它将使用该异常取消它的父协程。
         *    当父协程的所有子协程都结束后，异常才会被父协程处理
         */
        private fun CancelAndException() = runBlocking {
            val parentJob = launch {
                val childJob = launch {
                    try {
                        delay(Long.MAX_VALUE)
                    } catch (e: Exception) {
                        println("打印异常：$e")
                    } finally {
                        println("childJob is cancelled")
                    }
                }
                yield()
                println("Cancelling childJob")
                childJob.cancelAndJoin()
                yield()
                println("parentJob is cancelled")
            }
            parentJob.join()
        }
        //1、第二个子协程先抛异常；2、第一个子协程也会被取消；3、父协程等所哟子协程处理完毕后，再抛异常
        private fun CancelAndException2() = runBlocking {
            val handlerException=CoroutineExceptionHandler(){_,e->
                println("父协程捕获异常: $e")
            }
            val job = GlobalScope.launch(handlerException) {
                launch {
                    try {
                        delay(Long.MAX_VALUE)
                    } finally {
                        withContext(NonCancellable) {
                            println("Children are cancelled, but exception is not handled until all children terminate")
                            delay(100L)
                            println("The first child finished its non cancelled block")
                        }
                    }
                }
                launch {
                    delay(10L)
                    println("Second child throws an exception")
                    throw ArithmeticException()
                }
            }
            job.join()
        }
    }
}