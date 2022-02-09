package com.dn.coroutine.docs

import kotlinx.coroutines.*

/**
 * @Author zhangshuai
 * @Date 2021/9/5
 * @Emial zhangshuai@dushu365.com
 * @Description 取消和超时
 */
class CancelAndTimeOut {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
//            CancelCoroutinesExe()
//            CancelIsCollaborative()
//            CancelableCalculationCode()
//            ReleaseSourceInFinally()
//            RunNonCancellableCodeBlock()
//            RunNonCancellableCodeBlock()
            WithTimeout()
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
        }

        /**
         * 协程是协作的
         */
        fun CancelIsCollaborative() = runBlocking {
            val startTime = System.currentTimeMillis()
            val job = launch(Dispatchers.Default) {
                var nextPrintTime = startTime
                var i = 0
                while (i < 5) { //一个执行计算的循环，只是为了占用CPU
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
         * 运行不能取消的代码块
         */
        fun RunNonCancellableCodeBlock() = runBlocking {
            val job = launch {
                try {
                    repeat(1000) {
                        println("job: I'm sleeping $it")
                        delay(500L)
                    }
                } finally {
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
            }
            println("Result is $result")


            //ithTimeout 抛出了 TimeoutCancellationException，它是 CancellationException 的子类
//            withTimeout(1300L){
//                repeat(1000){
//                    println("job: I'm sleeping $it")
//                    delay(500L)
//                }
//            }
        }
    }
}