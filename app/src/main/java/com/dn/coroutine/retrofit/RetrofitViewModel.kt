package com.dn.coroutine.retrofit

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.dn.coroutine.retrofit.model.HpImageArchiveModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * @author zhangshuai
 * @date 2022/4/23 星期六
 * @email zhangshuai@dushu365.com
 * @description
 */
class RetrofitViewModel(application: Application) : AndroidViewModel(application) {

    val mLiveData = MutableLiveData<HpImageArchiveModel>()

    fun getData(count: String) {
        viewModelScope.launch {
            flow {
                val data = instance.getInfo("js", count, count)
                emit(data)
            }.flowOn(Dispatchers.IO)
                .catch { e ->
                    e.printStackTrace()
                    Log.e("print_logs", "请求异常：$e")
                }.collect {
                    mLiveData.value = it
                }
        }
    }
}