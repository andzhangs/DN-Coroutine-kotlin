package com.dn.coroutine.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.dn.coroutine.room.AppDataBase
import com.dn.coroutine.room.dao.UserDao
import com.dn.coroutine.room.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

/**
 * @author zhangshuai
 * @date 2022/4/22 星期五
 * @email zhangshuai@dushu365.com
 * @description
 */
class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val mDataBase = AppDataBase.getInstance(getApplication())

    private val mUserDao: UserDao = mDataBase.userDao()

    fun insert(uid: String, firstName: String, lastName: String) {
        viewModelScope.launch {
            val user = User(uid.toInt(), firstName, lastName)
            mUserDao.insert(user)
        }
    }

    fun getUserById(userId: String): Flow<User> {
        return mUserDao.getUserById(userId.toInt())
            .catch { e -> Log.e("print_logs", "UserViewModel::getUserById::e= $e") }
            .flowOn(Dispatchers.IO)
    }

    fun getAll(): Flow<List<User>> {
        return mUserDao.getAll().catch { e ->
            Log.e("print_logs", "UserViewModel::getAll::e= $e")
        }.flowOn(Dispatchers.IO)
    }

}