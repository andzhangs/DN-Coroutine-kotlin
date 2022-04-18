package com.safetyprotection.and.reverseanalysis.coroutine_lsn2.docs

import kotlinx.coroutines.*

/**
 * @Author zhangshuai
 * @Date 2021/9/5
 * @Emial zhangshuai@dushu365.com
 * @Description 协程与线程
 */
class CoroutineWithThread {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {

            schedulerAndThread()

            differentThreadJump()

            ThreadLocalData()
        }

        /**
         * 调度器与线程
         */
        private fun schedulerAndThread() = runBlocking {
            println("调度器与线程")
            // 运行在父协程的上下文中，即 runBlocking 主协程
            val job1 = launch {
                println("\t\t\tjob1-main runBlocking      : I'm working in thread ${Thread.currentThread().name}")
            }
            // 不受限的——将工作在主线程中
            val job2 = launch(Dispatchers.Unconfined) {
                println("\t\t\tjob2-Unconfined            : I'm working in thread ${Thread.currentThread().name}")
            }
            // 将会获取默认调度器
            val job3 = launch(Dispatchers.Default) {
                println("\t\t\tjob3-Default               : I'm working in thread ${Thread.currentThread().name}")
            }
            val job4 = GlobalScope.launch {
                println("\t\t\tjob4-GlobalScope           : I'm working in thread ${Thread.currentThread().name}")
            }
            // 将使它获得一个新的线程
            val job5 = launch(newSingleThreadContext("MyOwnThread")) {
                println("\t\t\tjob5-newSingleThreadContext: I'm working in thread ${Thread.currentThread().name}")
            }

            val job6 = coroutineScope {
                launch {
                    println("\t\t\tjob6-coroutineScope        : I'm working in thread ${Thread.currentThread().name}")
                }
            }

            job1.join()
            job2.join()
            job3.join()
            job4.join()
            job5.join()
            job5.cancel()
            job6.join()
        }

        /**
         * 在不同线程间跳转
         */
        private fun differentThreadJump() {
            newSingleThreadContext("Ctx1").use { ctx1 ->
                newSingleThreadContext("Ctx2").use { ctx2 ->
                    runBlocking(ctx1) {
                        println("Started in Ctx1 ${Thread.currentThread()}")
                        withContext(ctx2) {
                            println("Started in Ctx2 ${Thread.currentThread()}")
                        }
                        println("Back to ctx1")
                    }
                }
            }
        }

        /**
         * 线程局部数据
         */
        private suspend fun CoroutineScope.ThreadLocalData() {
            val threadLocal = ThreadLocal<String?>()
            threadLocal.set("main")
            println("Pre-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
            val job = launch(Dispatchers.Default + threadLocal.asContextElement(value = "launch")) {
                println("Launch start, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
                yield()
                println("After yield, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
            }
            job.join()
            println("Post-main, current thread: ${Thread.currentThread()}, thread local value: '${threadLocal.get()}'")
        }
    }
}