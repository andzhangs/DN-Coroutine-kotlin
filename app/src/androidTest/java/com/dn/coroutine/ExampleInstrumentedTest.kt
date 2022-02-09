package com.dn.coroutine

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.dn.coroutine", appContext.packageName)

        testKotlinChannel()
    }

    @Test
    fun testKotlinChannel()= runBlocking {
        val channel=Channel<Int>()
        //生产者
        val producer= coroutineScope {
            launch {
                var i=0
                while (true){
                    delay(1000)
                    channel.send(++i)
                    println("发送 $i")
                }
            }
        }

        val consumer=coroutineScope {
            launch {
                val element=channel.onReceive
                println("接收：$element")
            }
        }

        joinAll(producer,consumer)
    }
}