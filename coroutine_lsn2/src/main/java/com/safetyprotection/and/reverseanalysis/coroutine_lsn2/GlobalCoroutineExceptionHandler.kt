package com.safetyprotection.and.reverseanalysis.coroutine_lsn2

import android.util.Log
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.CoroutineContext

/**
 * @author zhangshuai
 * @date 2022/4/18 星期一
 * @email zhangshuai@dushu365.com
 * @description
 */
//监听未被捕获的异常
open class GlobalCoroutineExceptionHandler : CoroutineExceptionHandler {
    /**
     * A key of this coroutine context element.
     */
    override val key: CoroutineContext.Key<*>
        get() = CoroutineExceptionHandler

    /**
     * Handles uncaught [exception] in the given [context]. It is invoked
     * if coroutine has an uncaught exception.
     */
    override fun handleException(context: CoroutineContext, exception: Throwable) {
        Log.e("print_logs", "GlobalCoroutineExceptionHandler::接收未捕获异常: $exception")
    }
}