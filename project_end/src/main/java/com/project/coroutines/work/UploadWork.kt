package com.project.coroutines.work

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

/**
 * @author zhangshuai
 * @date 2022/5/5 星期四
 * @email zhangshuai@dushu365.com
 * @description
 */
@HiltWorker
class UploadWork @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted parameters: WorkerParameters
) : Worker(context, parameters) {

    override fun doWork(): Result {
        Log.i("print_logs", "UploadWork::doWork")
        return Result.success()
    }
}