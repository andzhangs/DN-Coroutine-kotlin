package com.zs.coroutine.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zs.coroutine.api.data.UserDataModel
import com.zs.coroutine.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * @author zhangshuai
 * @date 2022/4/16 星期六
 * @email zhangshuai@dushu365.com
 * @description
 */
class MainViewModel : ViewModel() {
    private val _userRepository = UserRepository()

    private val _userLiveData = MutableLiveData<UserDataModel>()
    fun getUserLiveData(): LiveData<UserDataModel> = _userLiveData

    fun getUser(userName: String) {
        viewModelScope.launch {
            val result=_userRepository.getUser(userName)
            Log.i("print_logs", "MainViewModel::getUser: $result")
            _userLiveData.value = result
        }
    }
}