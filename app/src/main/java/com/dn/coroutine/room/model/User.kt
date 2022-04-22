package com.dn.coroutine.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author zhangshuai
 * @date 2022/4/22 星期五
 * @email zhangshuai@dushu365.com
 * @description
 */
@Entity(tableName = "userTb")
data class User(
    @PrimaryKey val userId: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String
){
    override fun toString(): String {
        return "User(userId=$userId, firstName='$firstName', lastName='$lastName')"
    }
}