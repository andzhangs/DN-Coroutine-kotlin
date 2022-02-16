package com.dn.coroutine.closeable

import java.lang.Exception

/**
 * @Author zhangshuai
 * @Date 2022/2/9
 * @Emial zhangshuai@dushu365.com
 * @Description
 */
class TestAutoCloseable {

    class MyResource : AutoCloseable {

        fun doSomeThing() {
            println("TestAutoCloseable->MyResource：doSomeThing『做任何操作』")
        }

        override fun close() {
            println("TestAutoCloseable->MyResource：close『做任何操作』")
        }

    }

    companion object {

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                MyResource().use { mr -> mr.doSomeThing() }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                println("TestAutoCloseable->main.finally....")
            }

            //lambda表达式
            fun hello(): () -> Unit ={
                println("hello ,world")
            }
            //调用
            hello().invoke()
        }


    }
}