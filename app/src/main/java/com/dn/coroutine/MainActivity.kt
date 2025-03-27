package com.dn.coroutine

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.dn.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withTimeoutOrNull
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {

    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("coroutine")
        }
    }

    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        var i = 10
        val n = ++i % 5

        Log.i("print_logs", "MainActivity::onCreate: i= $i, n= $n")

        val m = 456
        val a = m % 10
        val b = m / 10
        val c = (m - m / 100 * 100) % 10
        Log.i("print_logs", "MainActivity::onCreate: a=$a, b=$b, c=$c")

        val job=lifecycleScope.launch(Dispatchers.IO) {
            test()
        }

        runBlocking {
            delay(500L)
            job.cancel()
            delay(1000)
            Log.d("print_logs", "MainActivity::runInterruptible: 完成")
        }
    }

    suspend fun test(){
        //确保每一次操作地耗时不要太长，即有一个超时失败地机制,withTimeout 在超时时会抛出异常，而 withTimeoutOrNull 只会返回一个 null 值
        val result=withTimeoutOrNull(500L){
            runInterruptible {
                while (true){
                    Thread.sleep(100L)
                    if (BuildConfig.DEBUG) {
                        Log.i("print_logs", "MainActivity::test: do something.")
                    }
                }
            }
        }
        if (BuildConfig.DEBUG) {
            Log.e("print_logs", "MainActivity::test: $result")
        }
    }

    private fun future() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //创建没有返回值的异步任务，类似ExecutorService submit(Runnable task)方法
            CompletableFuture.runAsync {
                Log.i("print_logs", "runAsync: 提交异步任务")
            }

            //创建带有返回值的异步任务，类似方法ExecutorService的 submit(Callable<T> task)方法
            val supplyAsync: CompletableFuture<Int> = CompletableFuture.supplyAsync {
                try {
                    TimeUnit.SECONDS.sleep(2)
                } catch (e: Exception) {
                    e.printStackTrace();
                } finally {
                    Log.i("print_logs", "supplyAsync: ${Thread.currentThread()}")
                }
                return@supplyAsync 1
            }

            //表示获取上一个任务的执行结果作为新任务的执行参数，有返回值
            val thenApply = supplyAsync.thenApply {
                Log.i("print_logs", "thenApply上个任务的结果: $it")
                return@thenApply 2
            }

            //接受上一个任务的结果作为参数，但是没有返回值
            val thenAccept = thenApply.thenAccept {
                Log.i("print_logs", "thenAccept上个任务的结果: $it")
            }

            //单纯的等待上个任务执行完成，不接受参数，也没有返回值
            //thenRunAsync也是和thenRun类似，不过回调任务是使用新线程去执行
            val thenRun = thenAccept.thenRun {
                Log.i("print_logs", "thenRun上一个任务是thenAccept")
            }

            Log.i("print_logs", "MainActivity::future: ${thenRun.get()}")


            //创建带有返回值的异步任务，类似方法ExecutorService的 submit(Callable<T> task)方法
            val supplyAsyncException: CompletableFuture<Int> = CompletableFuture.supplyAsync {
                try {
                    TimeUnit.SECONDS.sleep(2)
                } catch (e: Exception) {
                    e.printStackTrace();
                } finally {
                    Log.i("print_logs", "supplyAsyncException: ${Thread.currentThread()}")
                }
                throw RuntimeException("发送一次异常")
                return@supplyAsync 1
            }

            //exceptionally主要是异常回调，即发生异常后将异常作为参数传入，需要注意的不管有没有发生异常都是执行异常回调的方法
            val exceptionally = supplyAsyncException.exceptionally { throwable: Throwable ->
                Log.i("print_logs", "exceptionally：捕获到的异常: ${throwable.message}")
                return@exceptionally 2
            }
            Log.e("print_logs", "exceptionally：异常结果: ${exceptionally.get()}")

            //whenComplete 算是 exceptionally和thenApply的结合，即将任务执行的结果和异常作为回到方法的参数，如果没有发生异常则异常参数为null
            try {
                val whenComplete = supplyAsyncException.whenComplete { result, throwable ->
                    Log.i("print_logs", "whenComplete: result=$result,throwable=$throwable")
                }
                whenComplete.get()
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("print_logs", "抓取异常: $e")
            }

            //与whenComplete一样，不同的是handle返回的结果是异步回调返回的结果和异常，
            //而不是像whenComplete那样返回的是异步任务的结果和异常
            val handle = supplyAsyncException.handle { t, u ->
                Log.i("print_logs", "handle: t=$t, u=$u")
            }
            Log.e("print_logs", "handler：异常结果: ${handle.get()}")
        }
    }
}