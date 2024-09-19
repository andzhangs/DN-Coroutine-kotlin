package com.dn.coroutine.download

import android.util.Log
import com.dn.coroutine.utils.copyTo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

/**
 * @author zhangshuai
 * @date 2022/4/22 星期五
 * @email zhangshuai@dushu365.com
 * @description
 */
object DownloadManager {

    /**
     * @param url 下载地址
     * @param file 下载文件
     */
    @JvmStatic
    fun download(url: String, file: File): Flow<DownloadStatus> {
        return flow {
            val request = Request.Builder().url(url).get().build()
            val newCall = OkHttpClient.Builder().build().newCall(request)
            val response = newCall.execute()
            if (response.isSuccessful) {
                response.body!!.let { body ->
                    val total = body.contentLength()
                    Log.i("print_logs", "total: $total")
                    //文件读写操作
                    file.outputStream().use { output ->
                        val input = body.byteStream()
                        var emittedProgress = 0L
                        input.copyTo(output) { bytesCopied ->
                            Log.i("print_logs", "bytesCopied: $bytesCopied")
                            val process = bytesCopied / total * 100
                            if (process - emittedProgress > 5) { //每5个进度发一次
                                kotlinx.coroutines.delay(100L) //模拟延时/数据大等情况
                                emit(DownloadStatus.Progress(process.toInt()))
                                emittedProgress = process
                            }

//                            if (bytesCopied >= 10000){  //手动停止
//                                newCall.cancel()
//                                Log.e("print_logs", "DownloadManager::download: 取消下载")
//                            }
                        }
                    }
                }
                emit(DownloadStatus.Done(file))
            }else{
                val e = IOException(response.toString())
                emit(DownloadStatus.Error(e))
                throw e
            }

        }.catch {
            file.delete()
            emit(DownloadStatus.Error(it))
        }.flowOn(Dispatchers.IO)
    }

}