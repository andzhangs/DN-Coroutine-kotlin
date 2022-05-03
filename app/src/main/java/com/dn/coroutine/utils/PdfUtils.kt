package com.dn.coroutine.utils

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
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.FileOutputStream
import java.io.OutputStream

/**
 * @author zhangshuai
 * @date 2022/4/26 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
class PdfUtils(private val application: Application) {

    fun createPdfFile(contentView: View, outputString: OutputStream, input: ParcelFileDescriptor) {
        //写入
        val pageDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(100, 200, 10).create()
        val page = pageDocument.startPage(pageInfo)
        contentView.draw(page.canvas)
        pageDocument.finishPage(page)
        pageDocument.writeTo(outputString)
        pageDocument.close()
        //读取
        val pdfRenderer = PdfRenderer(input)
        val pageCount = pdfRenderer.pageCount
        val mBitmap = BitmapFactory.decodeFile("")
        for (i in 0..pageCount) {
            pdfRenderer.openPage(i).apply {
                render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                close()
            }
        }
        pdfRenderer.close()
    }

    fun Pdf() {
        Thread(Runnable {
            val file = application.getExternalFilesDir("")
            val out = FileOutputStream(file)
            val document = PdfDocument()
            val windowSize = Point()
            val windowManager = application.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        }).start()
    }
}