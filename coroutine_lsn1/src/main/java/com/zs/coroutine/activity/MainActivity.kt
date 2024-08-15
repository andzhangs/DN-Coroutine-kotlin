package com.zs.coroutine.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.zs.coroutine.api.UserServiceApi
import com.zs.coroutine.api.data.UserDataModel
import com.zs.coroutine.api.userServiceApi
import com.zs.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import kotlin.coroutines.*

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private lateinit var mBinding: ActivityMainBinding

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
    }


    /**
     *                                      优美的分割线
     * ---------------------------------------------------------------------------------------------
     *
     * ---------------------------------------------------------------------------------------------
     */
    /**
     * 协程与异步任务对比
     */
    fun click1(view: View) {
//            userServiceApi.getUserInfo(UserServiceApi.USER_NAME)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe {
//                    Log.i("print_logs", "MainActivity::onCreate: $it")
//                }

//            CoroutineScope(Dispatchers.IO).launch {
//                Log.i("print_logs", "MainActivity::onCreate: ${Thread.currentThread()}")
//                val result = userServiceApi.getUserDataModel(UserServiceApi.USER_NAME)
//                Log.i("print_logs", "MainActivity::onCreate: $result")
//            }


        //GlobalScope默认是在非UI线程
        GlobalScope.launch {
            Log.i("print_logs", "MainActivity::请求线程-1: ${Thread.currentThread()}")
            val result =userServiceApi.getUserDataModel(UserServiceApi.USER_NAME)
            Log.i("print_logs", "MainActivity::onCreate:结果： $result")
            withContext(Dispatchers.Main){
                Toast.makeText(this@MainActivity, "访问成功：${result.name}", Toast.LENGTH_SHORT).show()
            }
        }

        //协程让异步逻辑同步化
        //协程核心点：函数或者一段程序能够被挂起，，稍后再在挂起的位置恢复
//        CoroutineScope(Dispatchers.Main).launch {
//            Log.i("print_logs", "MainActivity::请求线程-1: ${Thread.currentThread()}")
//            val result =userServiceApi.getUserDataModel(UserServiceApi.USER_NAME)
//            Log.i("print_logs", "MainActivity::onCreate:结果： $result")
//        }
    }

    /**
     *                                      优美的分割线
     * ---------------------------------------------------------------------------------------------
     *
     * ---------------------------------------------------------------------------------------------
     */
    /**
     * 协程挂起与恢复
     * 常规函数基础操作包括：invoke(或call)和return,协程新增了suspend和resume
     * suspend：也称为挂起和暂停，用于暂停执行当前协程，并保存所有局部变量
     * resume：用于让已暂停的协程从其暂停处继续执行
     */
    fun click2(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            getUser()
        }
    }

    //第一步
    private suspend fun getUser() {
        val user = get()
        show(user)
    }

    //第二步
    private suspend fun get() = withContext(Dispatchers.IO) {
        userServiceApi.getUserDataModel(UserServiceApi.USER_NAME)
    }

    //第三步
    private suspend fun show(userDataModel: UserDataModel) {
        Log.i("print_logs", "MainActivity::onCreate:结果： $userDataModel")

        val result = userServiceApi.getUserDataMode2(UserServiceApi.USER_NAME)

        Log.e("print_logs", "MainActivity::show: ${Thread.currentThread()}")
        Log.i("print_logs", "MainActivity::show: ${result.await()}")
    }
    /**
     *                                      优美的分割线
     * ---------------------------------------------------------------------------------------------
     *
     * ---------------------------------------------------------------------------------------------
     */
    /**
     * 挂起与阻塞对比
     */
    fun click3(view: View) {
        CoroutineScope(Dispatchers.Main).launch {
            //挂起
            Log.i("print_logs", "MainActivity::click3: 挂起开始")
            delay(12000L)
            Log.i("print_logs", "MainActivity::click3: 挂起结束")
        }
        Log.i("print_logs", "MainActivity::click3: 外部")

//        Log.i("print_logs", "MainActivity::click3: 阻塞开始")
//        Thread.sleep(12000L)
//        Log.i("print_logs", "MainActivity::click3: 阻塞结束")
    }

    /**
     *                                      优美的分割线
     * ---------------------------------------------------------------------------------------------
     * 协程分为两个层次：
     * 1、基础设施层：标准库的协程API，主要是对协程提供了概念和语义上最基本支持
     * 2、业务框架层：协程的上层框架支持
     * 注意：协程的挂起点就是通过continuation保存起来的
     * ---------------------------------------------------------------------------------------------
     */
    fun click4(view: View) {
        //协程体
        val continuation = suspend {
            Log.i("print_logs", "MainActivity::click4: ${Thread.currentThread()}")
            5
        }.createCoroutine(object : Continuation<Int> {
            /**
             * The context of the coroutine that corresponds to this continuation.
             */
            override val context: CoroutineContext
                get() = Dispatchers.Main

            /**
             * Resumes the execution of the corresponding coroutine passing a successful or failed [result] as the
             * return value of the last suspension point.
             */
            override fun resumeWith(result: Result<Int>) {
                Log.i("print_logs", "MainActivity::resumeWith: ${result.getOrNull()}")
            }
        })
        //启动协程
        continuation.resume(Unit)

    }

    /**
     *                                      优美的分割线
     * ---------------------------------------------------------------------------------------------
     * Dispatchers.Main    ：Android的主线程，用来处理UI交互和一些轻量级的任务：调用suspend函数、调用UI函数、更新LiveData
     * Dispatchers.IO      ：非主线程，专为磁盘和网络IO进行了优化：数据库、文件读写、网络处理
     * Dispatchers.Default ：非主线程，专为CPU密集型任务进行了优化：数组排序、JSON数据解析、处理差异判断
     * ---------------------------------------------------------------------------------------------
     */
    fun click5(view: View) {

    }

    /**
     *                                      优美的分割线
     * ---------------------------------------------------------------------------------------------
     * 任务泄漏：
     *  1、当某个协程任务丢失，无法追踪，会导致内存、CPU、磁盘等资源浪费，甚至发送一个无用的网络请求。
     *  2、为了避免协程泄漏，kotlin引入了'结构化并发机制'
     *      结构化并发可以做到：
     *          1、取消任务：当某个任务不再需要时取消它
     *          2、追踪任务：当任务正在执行时，追踪它
     *          3、发出错误信号：当协程失败时，发出错误信号表明有错误发生
     *
     * https://developer.android.google.cn/kotlin/coroutines/coroutines-adv
     *
     * CoroutineScope，它会跟踪所有协程，还可以取消由他所启动的所有协程
     * 常用相关API：
     *      GlobalScope：生命周期是process级别，即使Activity和Fragment已经销毁，协程仍然在执行
     *      MainScope：在Activity中使用，可以在onDestroy()中取消协程
     *      viewModelScope：只能在ViewModel中使用，绑定ViewModel的生命周期
     *      lifecycleScope：只能在Activity和Fragment中使用，会绑定Activity和Fragment的生命周期
     * ---------------------------------------------------------------------------------------------
     */
    fun click6(view: View) {
        //方式一：
//        mainScope.launch {
//            //retrofit检测到接口为挂起函数，自动启动一个IO调度器的协程
//            val result = userServiceApi.getUserDataModel(UserServiceApi.USER_NAME)
//            Log.i("print_logs", "MainActivity::click6: $result")
//
//            //协程取消：点击后，在按返回键
////            try {
////                delay(10000L)
////            }catch (e:Exception){
////                e.printStackTrace()
////                Log.e("print_logs", "MainActivity::click6: 协程取消")
////            }
//        }
        //方式二：当前类继承 CoroutineScope by MainScope()，见17行
        launch {
            val result = userServiceApi.getUserDataModel(UserServiceApi.USER_NAME)
            Log.i("print_logs", "MainActivity::click6: $result")
        }
    }

    private val mainScope: CoroutineScope by lazy { MainScope() + CoroutineName(MainActivity::class.java.name) }

    override fun onDestroy() {
        super.onDestroy()
        mainScope.cancel()
        cancel()
    }

    /**
     *                                      优美的分割线
     * ---------------------------------------------------------------------------------------------
     * Demo
     * ---------------------------------------------------------------------------------------------
     */
    fun click7(view: View) {
        startActivity(Intent(this, MainActivity2::class.java))
    }

}