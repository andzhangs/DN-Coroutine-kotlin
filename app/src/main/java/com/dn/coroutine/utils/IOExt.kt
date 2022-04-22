package com.dn.coroutine.utils

import java.io.InputStream
import java.io.OutputStream

/**
 * @author zhangshuai
 * @date 2022/4/22 星期五
 * @email zhangshuai@dushu365.com
 * @description 扩展函数
 */

inline fun InputStream.copyTo(
    out: OutputStream,
    bufferSize: Int = DEFAULT_BUFFER_SIZE,
    progress: (Long) -> Unit
) {
    var bytesCopied: Long = 0L
    val buffer = ByteArray(bufferSize)
    var bytes = read(buffer)
    while (bytes >= 0) {
        out.write(buffer, 0, bytes)
        bytesCopied += bytes
        bytes = read(buffer)

        progress(bytesCopied)
    }
}