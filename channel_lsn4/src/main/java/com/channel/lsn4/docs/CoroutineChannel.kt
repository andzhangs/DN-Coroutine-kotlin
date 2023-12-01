package com.channel.lsn4.docs

import com.channel.lsn4.api.data.UserDataModel
import com.google.gson.Gson
import com.zs.coroutine.api.UserServiceApi
import com.zs.coroutine.api.userServiceApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * @author zhangshuai
 * @date 2022/4/21 星期四
 * @email zhangshuai@dushu365.com
 * @description
 */
object CoroutineChannel {

    /**
     * Channel
     * 是一个并发安全队列，可以用来连接协程，实现不同协程的通信
     * 容量：
     *  队列中一定存在缓冲区，那么一旦这个缓冲区满了，并且也一直没有人调用receive并取走函数，send就需要挂起。
     *  故意让接收端的节奏放慢，发现send总是会挂起，直到receive之后才会继续往下执行
     *
     * 迭代：
     *  本身确实像序列，所以我们在读取的时候可以直接获取一个Channel的iterator
     *
     * produce：生产者协程 ReceiveChannel
     * actor：消费者协程 SendChannel
     *  1、构造生产者与消费者的便捷方法
     *  2、通过produce方法启动一个生产者协程，并返回一个ReceiveChannel，其他协程就可以用这个Channel来接收数据。
     *     反过来
     *
     * 关闭：
     *  1、produce与actor返回的Channel都会随着对应的协程执行完毕后关闭，所以Channel才被称为热数据流
     *  2、对于一个Channel，如果我们调用了它close()方法，它会立即停止接收元素，即此时它的isCloseForSend()会立即返回true。
     *     而由于channel缓冲区的存在，这时候可能有一些元素乜有处理完，因此要等所有的元素都被读取之后isCloseForReceive()才返回true。
     *  3、Channel的生命周期最好有主导方来维护，建议由主导的一方实现关闭
     *
     * 【以上是一个接收端接收到了，其他接收端不能接收】
     *
     * BroadcastChannel 广播渠道 一对多
     *  前面提到，发送端和接收端在channel中存在一对多的情形，从数据处理本身来讲，虽然有多个接收端，但是同一个元素只会被一个接收端督读到。
     *  广播则不然，多个接收端不存在互斥行为。
     *
     * 多路复用： 复用多个await，运用：select{}-onAwait{}
     *  数据通信系统或计算机网络系统中，传输媒体的带宽或容量往往大于传输单一信号的需求，wield有效地利用通信线路，
     *  希望一个信道同时传输多路信号，这就是是所谓的【多路复用技术Multiplexing】
     *
     * 复用多个Channel,类似多个await
     *
     * SelectClause
     *  我们怎么知道哪些事件可以被Select？其实所有能够被select的事件都是SelectClauseN类型，包括：
     *      1、SelectClause0：对应事件没有返回值，例如join，那么onJoin就是SelectClauseN类型，使用时，onJoin的参数就是一个无参函数。
     *      2、SelectClause1：对应事件有返回值，前面的onAwait和onReceive都是此类情况。
     *      3、SelectClause2：对应事件有返回值，此外还需要一个额外的参数，例如Channel.onSend有两个参数，
     *          第一个是Channel数据类型的值，表示即将发送的值；
     *          第二个是发送成功时的回调参数。
     *  如果我们想要确认挂起函数是否支持select，只需要查看其是否存在对应SelectClauseN类型可回调即可。
     *
     *  多路复用-Flow
     *
     *
     *
     */

    @JvmStatic
    fun main(args: Array<String>) {
        first()
        second()
        channelIterator()
        produceReceiveChannel()
        actorSendChannel()
        closeChannel()
        broadcastChannel()
        multiplexingSelectOnAwait()
        multiplexingChannel()
        selectClause0()
        selectClause2()
//        multiplexingFlow()
    }

    private fun first() = runBlocking {
        val channel = Channel<Int>()
        //生产者
        val productor = GlobalScope.launch {
            for (i in 1..3) {
                withContext(Dispatchers.IO) {
                    print("发送：$i")
                    channel.send(i)
                    delay(500L)
                }
            }
        }

        //消费者
        val consumer = GlobalScope.launch {
            while (true) {
                val element = channel.receive()
                println("接收：$element")
            }
        }
        joinAll(productor, consumer)
    }

    /**
     * 容量
     */
    fun second() = runBlocking {
        println("------------------ 容量 ------------------")
        val channel = Channel<Int>()
        //生产者
        val productor = GlobalScope.launch {
            for (i in 1..3) {
                withContext(Dispatchers.IO) {
                    delay(1000L)
                    val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
                    print("发送：$i [$date] ")
                    channel.send(i)
                }
            }
        }
        //消费者
        val consumer = GlobalScope.launch {
            while (true) {
                delay(2000L)
                val element = channel.receive()
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
                println("接收：$element  [$date]")
            }
        }
        joinAll(productor, consumer)
    }


    /**
     * 迭代
     * 适用场景：生产比较快，但是消费比较慢
     */
    fun channelIterator() = runBlocking {
        val channel = Channel<Int>(Channel.UNLIMITED)
        //生产者
        val productor = GlobalScope.launch {
            for (i in 1..5) {
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
                println("发送-> ${i * i} [$date] ")
                channel.send(i * i)
            }
        }
        //消费者
        val consumer = GlobalScope.launch {
            //方式一：
//            val iterator=channel.iterator()
//            while (iterator.hasNext()) {
//                delay(2000L)
//                val element = iterator.next()
//                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
//                println("<-接收：$element  [$date]")
//            }
            //方式二：
            for (element in channel) {
                delay(2000L)
                val date = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA).format(Date())
                println("<-接收：$element  [$date]")
            }
        }
        joinAll(productor, consumer)
    }

    /**
     *  produce 生成者Channel
     */
    @OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun produceReceiveChannel() = runBlocking {
        val receiveChannel: ReceiveChannel<Int> = GlobalScope.produce {
            repeat(10) {
                delay(1000L)
                send(it)
                println("发送-> $it")
            }
        }

        val consumer = GlobalScope.launch {
            for (i in receiveChannel) {
                println("<-接收：${i}")
            }
        }

        consumer.join()
    }

    /**
     * actor 消费者Channel
     */
    @OptIn(ObsoleteCoroutinesApi::class, DelicateCoroutinesApi::class)
    fun actorSendChannel() = runBlocking {
        val sendChannel: SendChannel<Int> = GlobalScope.actor {
            while (true) {
                val element = receive()
                println("<-接收：$element")
            }
        }

        val produce = GlobalScope.launch {
            for (k in 0..3) {
                println("发送->：$k")
                sendChannel.send(k)
            }
        }
        produce.join()
    }

    /**
     * 关闭
     */
    fun closeChannel() = runBlocking {
        val channel = Channel<Int>(3)
        //生产者
        val productor = GlobalScope.launch(Dispatchers.Default) {
            for (i in 1..3) {
                print("发送：$i ")
                channel.send(i)
//                delay(500L)
            }
            channel.close()
            println()
            println(
                """close channel. 
                | - CloseForSend：${channel.isClosedForSend}.
                | - CloseForReceive：${channel.isClosedForReceive}.
            """.trimMargin()
            )
        }

        //消费者
        val consumer = GlobalScope.launch {
            for (element in channel) {
                delay(3000L)
                println("接收：$element")
            }
            println(
                """close channel. 
                | - CloseForSend：${channel.isClosedForSend}.
                | - CloseForReceive：${channel.isClosedForReceive}.
            """.trimMargin()
            )
        }
        joinAll(productor, consumer)
    }

    /**
     * 广播 ：一个发送端，多个接收端，并不互斥
     */
    fun broadcastChannel() = runBlocking {
//        val broadcastChannel = BroadcastChannel<Int>(Channel.BUFFERED)
        //Channel转换成BroadcastChannel
        val channel = Channel<Int>()
        val broadcastChannel = channel.broadcast(Channel.BUFFERED)

        val num = 3
        //生产者
        val productor = GlobalScope.launch {
            List(num) {
//                println("发送->：$it ")
                delay(100L)
                broadcastChannel.send(it)
            }
            broadcastChannel.close()
        }

        //消费者
        List(num) { index ->
            GlobalScope.launch {
                val channel = broadcastChannel.openSubscription()
                for (i in channel) {
                    println("<-接收：#$index, $i")
                }
            }
        }.joinAll()

        val properties = try {
            System.getProperty("kotlinx.coroutines.channels.defaultBuffer")
        } catch (e: SecurityException) {
            null
        }
        println("属性：$properties")
    }

    /**
     * 多路复用
     * 案例：两个API分享从网络和本地缓存中获取数据，期望哪个先返回就先用哪个做展示。
     */
    data class Response<T>(val value: T?, val isLocal: Boolean)

    //模拟请求本地缓存
    fun CoroutineScope.getDataFromLocal() =
        async(Dispatchers.IO) {
//        delay(2000L)  //故意延时，本地访问慢一下
            File("channel_lsn4/src/main/assets/UserDataModel.txt").readText()
                .let { Gson().fromJson(it, UserDataModel::class.java) }
        }


    //模拟网络请求数据
    fun CoroutineScope.getDataFromRemote(username: String = UserServiceApi.USER_NAME) =
        async(Dispatchers.IO) {
            userServiceApi.getUserDataModel(username)
        }


    fun multiplexingSelectOnAwait() = runBlocking {
        //通过延迟本地时间模拟
        GlobalScope.launch() {
            withContext(Dispatchers.IO) {
                val localRequest = getDataFromLocal()
                val remoteRequest = getDataFromRemote()
                val response = select<Response<UserDataModel>> {
                    localRequest.onAwait { Response(it, true) }
                    remoteRequest.onAwait { Response(it, false) }
                }
                println("返回值：${response.value}，${response.isLocal}")
            }
        }.join()
    }

    /**
     * 复用多个Channel
     */
    fun multiplexingChannel() = runBlocking {
        val channel = listOf(Channel<String>(), Channel<String>())
        GlobalScope.launch {
            delay(100L)
            channel[0].send("I am from channel-1")
        }

        GlobalScope.launch {
            delay(50L)
            channel[1].send("I am from channel-2")
        }

        val response = select<String> {
            channel.forEach { c ->
                c.onReceive { it }
            }
        }
        println("接收：$response")
    }

    fun selectClause0() = runBlocking {
        val job1 = GlobalScope.launch {
            delay(100L)
            println("I am from job-1")
        }

        val job2 = GlobalScope.launch {
            delay(50L)
            println("I am from job-2")
        }
        select<Unit?> {
            job1.onJoin { println("job1 is onJoin") }
            job2.onJoin { println("job2 is onJoin") }
        }

        delay(1000L)
    }

    /**
     * selectClause2
     */
    fun selectClause2() = runBlocking {
        val channel = listOf(Channel<String>(), Channel<String>())

        launch(Dispatchers.IO) {
            select<Unit?> {
                launch() {
                    delay(200L)
                    channel[0].onSend("channel-1") {
                        println("发送成功后的回调：channel-1")
                    }
                }

                launch {
                    delay(100L)
                    channel[1].onSend("channel-2") {
                        //发送成功后的回调
                        println("发送成功后的回调：channel-2")
                    }
                }
            }
        }

        launch {
            println("接收：${channel[0].receive()}")
        }

        launch {
            println("接收：${channel[1].receive()}")
        }

        delay(1000L)
    }

    /**
     * 多路复用-Flow
     */
    fun multiplexingFlow() = runBlocking {
        //函数->协程->Flow->Flow合并
//        coroutineScope {
//            withContext(Dispatchers.IO) {
                 ////通过作用域，将对应方法调用添加至list集合里
//                listOf(::getDataFromLocal, ::getDataFromRemote)
                 ////遍历集合每个方法，function 就为对应的某个方法
//                    .map { function ->
                        ////这里调用对应方法后，将返回的结果传至下个map里
//                        function.call(UserServiceApi.USER_NAME)
//                    }.map {  //这里对应deferred 表示对应方法返回的结果
//                        flow { emit(it.await()) }  //这里表示，得到谁，就通过flow 发射值
//                    }.merge()  //流 合并
//                    .collect {  //这里只管接收flow对应发射值
//                        println("打印：$it")
//                    }
//            }
//        }
    }
}
