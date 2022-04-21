package com.safetyprotection.and.reverseanalysis.coroutine_lsn2.docs

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.sync.withPermit
import java.util.concurrent.atomic.AtomicInteger

/**
 * @author zhangshuai
 * @date 2022/4/21 星期四
 * @email zhangshuai@dushu365.com
 * @description
 */
object CoroutineConcurrent {

    /**
     * 协程并发
     *
     *  并发安全：
     *     1、不安全的并发访问
     *     2、安全的并发访问
     *
     *  工具：
     *      1、Channel：并发安全的消息通道
     *      2、Mutex锁：轻量级锁。它的lock和unlock从语义上与线程锁比较类似，
     *                 之所以轻量是因为它在获取不到锁的时候不会阻塞线程，而是挂起等待锁的释放。
     *      3、Semaphore：轻量级信号量，信号量可以有多个，协程在获取到信号量后即可执行并发操作
     *                    当Semaphore的参数为1时，效果等价于Mutex。
     *
     *  避免访问外部可变状态
     *      编写函数时要求它不得访问外部状态，只能基于参数运算，通过返回值提供运算结果。
     *
     */
    @JvmStatic
    fun main(args: Array<String>) {
//        notSafeCoroutine()
//        safeCoroutine()
//        mutexCoroutine()
//        semaphoreCoroutine()
        accessOuterVariable()
    }

    /**
     * 并发安全-不安全的并发访问
     */
    fun notSafeCoroutine() = runBlocking {
        var count = 0
        List(1000) {
            GlobalScope.launch { count++ }
        }.joinAll()

        println("不安全输出：$count")
    }

    /**
     * 并发安全-安全的并发访问
     */
    fun safeCoroutine() = runBlocking {
        //原子性
        var count = AtomicInteger(0)
        List(1000) {
            GlobalScope.launch { count.incrementAndGet() }
        }.joinAll()

        println("AtomicInteger安全输出：${count.get()}")
    }

    /**
     * Mutex
     */
    fun mutexCoroutine() = runBlocking {
        //原子性
        var count = 0
        val mutex = Mutex()
        List(1000) {
            GlobalScope.launch {
                mutex.withLock {
                    count++
                }
            }
        }.joinAll()
        println("Mutex安全输出：${count}")
    }

    /**
     * Semaphore
     */
    fun semaphoreCoroutine() = runBlocking {
        //原子性
        var count = 0
        val semaphore = Semaphore(1) //当Semaphore的参数为1时，效果等价于Mutex。
        List(1000) {
            GlobalScope.launch {
                semaphore.withPermit {
                    count++
                }
            }
        }.joinAll()
        println("Semaphore安全输出：$count")
    }

    /**
     * 避免并发
     */
    fun accessOuterVariable() = runBlocking {
        var count = 0
        var result=count + List(1000) {
            GlobalScope.async { 1 }  //1000个1相加
        }.sumOf { it.await() }

        println("Semaphore安全输出：$result")
    }


}