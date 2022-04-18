package com.safetyprotection.and.reverseanalysis.coroutine_lsn2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import com.safetyprotection.and.reverseanalysis.coroutine_lsn2.docs.CoroutineWithException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val handlerException :CoroutineExceptionHandler by lazy {
        CoroutineExceptionHandler { _, throwable ->
            Log.e("print_logs", "MainActivity::打印异常: $throwable")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //捕获异常防止APP闪退
        findViewById<AppCompatButton>(R.id.acBtn_getException).setOnClickListener {
            GlobalScope.launch(handlerException) {
                Log.i("print_logs", "MainActivity::onCreate: click acBtn_getException")
                throw RuntimeException("自定义异常-1")
            }

            //全局异常处理
            CoroutineWithException.globalException()
        }
    }


}