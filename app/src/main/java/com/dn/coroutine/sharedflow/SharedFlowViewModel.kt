package com.dn.coroutine.sharedflow

import android.app.Application
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Point
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.view.View
import android.view.WindowManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * @author zhangshuai
 * @date 2022/4/26 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
class SharedFlowViewModel : ViewModel() {

    /**
     * SharedFlow会向从其中收集值的所有使用方发出数据
     * SharedFlow与StateFlow不同的点，
     * 1、SharedFlow初始化没有默认值，且会保留历史值
     * 2、StateFlow有默认值，且只保留最新的一个值
     */


    private lateinit var job: Job

    fun start() {
        job = viewModelScope.launch(Dispatchers.IO) {
            while (true) {
                LocalEventBus.postEvent(Event(System.currentTimeMillis()))
            }
            runBlocking {  }
        }
    }

    fun stop() {
        job.cancel()
    }
}