package com.zs.coroutine.repository

import com.zs.coroutine.api.userServiceApi
import com.zs.coroutine.api.data.UserDataModel

/**
 * @author zhangshuai
 * @date 2022/4/16 星期六
 * @email zhangshuai@dushu365.com
 * @description
 */
class UserRepository {

    suspend fun getUser(userName: String): UserDataModel {
        return userServiceApi.getUserDataModel(userName)
    }

}