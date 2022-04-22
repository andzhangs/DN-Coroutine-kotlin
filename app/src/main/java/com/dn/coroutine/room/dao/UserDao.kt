package com.dn.coroutine.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dn.coroutine.room.model.User
import kotlinx.coroutines.flow.Flow

/**
 * @author zhangshuai
 * @date 2022/4/22 星期五
 * @email zhangshuai@dushu365.com
 * @description
 */
@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User) //协程支持

    @Query("SELECT * FROM userTb")
    fun getAll(): Flow<List<User>>

    @Query("SELECT * FROM userTb where userId = :uId")
    fun getUserById(uId:Int):Flow<User>
}