package com.dn.coroutine.download

import java.io.File

/**
 * @author zhangshuai
 * @date 2022/4/22 星期五
 * @email zhangshuai@dushu365.com
 * @description
 */
sealed class DownloadStatus {

    object None : DownloadStatus()

    //下载进度
    data class Progress(val value: Int) : DownloadStatus()

    //下载完成
    data class Done(val file: File) : DownloadStatus()

    //下载失败
    data class Error(val throwable: Throwable) : DownloadStatus()
}
