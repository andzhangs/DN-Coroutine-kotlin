package com.safetyprotection.and.reverseanalysis.coroutine_lsn2.docs

import kotlinx.coroutines.*

/**
 * @Author zhangshuai
 * @Date 2021/9/5
 * @Emial zhangshuai@dushu365.com
 * @Description 协程上下文/作用域/调度器
 */
class CoroutineContextWithScope {

    /**
     * 协程上下文：
     *  1、Job：控制协程的生命周期
     *  2、CoroutineDispatcher：向合适的线程分发任务
     *  3、CoroutineName：协程的名称，调试的时候很有用
     *  4、CoroutineExceptionHandler：处理未被捕捉的异常
     */
    companion object {
        @JvmStatic
        fun main(args: Array<String>) = runBlocking {
            val job = CoroutineScope(Dispatchers.Default).launch {
                val s = try {
                    System.getProperty("kotlinx.coroutines.scheduler")
                } catch (e: SecurityException) {
                    println("异常打印：$e")
                }
                println("输出：$s， 当前线程：${Thread.currentThread().name}")
            }
            job.start()
            job.join()

            /**
             * 上下文中的作业
             */
            // 打印输出：BlockingCoroutine{Active}@7907ec20
            // CoroutineScope 中的 isActive 只是 coroutineContext[Job]?.isActive == true 的一种方便的快捷方式。
            println("this is ${coroutineContext[Job]}")


            ChildCoroutines()

            ParentCoroutines()

            RenameForCoroutine()

            CoroutineContext2()

            RunCoroutineScope()
        }

        /**
         * 协程上下文的继承
         */
        private suspend fun CoroutineScope.ChildCoroutines() {
            /**
             * 子协程
             * 当一个协程被其它协程在 CoroutineScope 中启动的时候， 它将通过 CoroutineScope.coroutineContext 来承袭上下文，
             * 并且这个新协程的 Job 将会成为父协程作业的 子 作业。
             * 当一个父协程被取消的时候，所有它的子协程也会被递归的取消。
             * 然而，当使用 GlobalScope 来启动一个协程时，则新协程的作业没有父作业。 因此它与这个启动的作用域无关且独立运作。
             */
            println("CoroutineScope.ChildCoroutines()：")
            val request = launch {
                //孵化了两个子作业, 其中一个通过 GlobalScope 启动
                GlobalScope.launch {
                    println("job1: I run in GlobalScope and execute independently!")
                    delay(1000)
                    println("job1: I am not affected by cancellation of the request")
                }
                //承袭了父协程的 上下文
                launch {
                    delay(100)
                    println("job2: I am a child of the request coroutine")
                    delay(1000)
                    println("job2: I will not execute this line if my parent request is cancelled")
                }
            }
            delay(500L)
            // 取消请求（request）的执行
            request.cancel()
            delay(1000L)
            println("main: Who has survived request cancellation?\n\t\t")
        }

        /**
         * 父协程的职责
         * 一个父协程总是等待所有的子协程执行结束。父协程并不显式的跟踪所有子协程的启动，
         * 并且不必使用 Job.join 在最后的时候等待它们：
         */
        private suspend fun CoroutineScope.ParentCoroutines() {
            println("CoroutineScope.ParentCoroutines()：")
            val request = launch {
                repeat(3) {
                    launch {
                        //延迟200毫秒、400毫秒、600毫秒的时间
                        delay((it + 1) * 200L)
                        println("Coroutine $it is done, 当前时间=${System.currentTimeMillis()}")
                    }
                }
                println("request: I'm done and I don't explicitly join my children that are still active")
            }
            request.join() // 等待请求的完成，包括其所有子协程
            println("Now processing of the request is complete\n\t\t")
        }

        /**
         * 命名协程以用于调试
         */
        private suspend fun CoroutineScope.RenameForCoroutine() {
            println("Started main coroutines")
            //运行俩个后台值计算
            val v1 = async(CoroutineName("v1coroutine")) {
                delay(500L)
                println("Computing V1，and in thread ${Thread.currentThread().name}")
                252
            }
            val v2 = async(CoroutineName("v2coroutine")) {
                delay(500L)
                println("Computing V2，and in thread ${Thread.currentThread().name}")
                6
            }
            println("The answer for v1/v2 =${v1.await().div(v2.await())}")

            launch(Dispatchers.Default + CoroutineName("test")) {
                println("I'm working in thread ${Thread.currentThread().name}")
            }
        }

        private suspend fun CoroutineScope.CoroutineContext2() {
            val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
                println("打印：$throwable")
            }
            val scope = CoroutineScope(Job() + Dispatchers.Main + coroutineExceptionHandler)
            val job=scope.launch(Dispatchers.IO) {
                println("新协程：${Thread.currentThread().name}")
            }
            job.start()
            job.join()
        }


        /**
         * 协程作用域与构建器
         *  1、runBlocking是常规函数，而coroutineScope是挂起函数
         *  2、它们都会等待其协程体以及所有子线程结束，主要区别在于runBlocking方法会阻塞当钱线程来等待，
         *     而coroutineScope只是挂起，会释放底层线程用于其他用途
         *  coroutineScope：一个线程失败了，其他所有兄弟协程也会被取消
         *  supervisorScope：一个线程失败了，不会影响其他兄弟协程
         */
        private suspend fun RunCoroutineScope() {
//            scope()

            val activity = Activity()
            activity.doSomeThing()
            println("Launched coroutines")
            delay(500L)
            activity.destory()
            println("Destroying activity!")
            delay(1000L)
        }

        private suspend fun scope() {
            //方式一
            coroutineScope {
                val job1 = launch {
                    delay(400L)
                    println("job1 finished")
                }
                val job2 = async {
                    delay(200L)
                    println("job2 finished")
                    println("job2 result")
                    throw IllegalArgumentException()
                }
            }

            //方式二
            supervisorScope {
                val job1 = launch {
                    delay(400L)
                    println("job1 finished")
                }
                val job2 = async {
                    delay(200L)
                    println("job2 finished")
                    println("job2 result")
                    throw IllegalArgumentException()
                }
            }
        }

        private class Activity {
            private val mainScope = CoroutineScope(Dispatchers.Default)

            fun doSomeThing() {
                //启动了 10 个协程，且每个都工作了不同的时长
                repeat(10) { i ->
                    mainScope.launch {
                        delay((i + 1) * 200L) // 延迟 200 毫秒、400 毫秒、600 毫秒等等不同的时间
                        println("Coroutine $i is done，${System.currentTimeMillis()}，${Thread.currentThread().name}")
                    }
                }
            }

            fun destory() {
                mainScope.cancel()
            }
        }
    }
}