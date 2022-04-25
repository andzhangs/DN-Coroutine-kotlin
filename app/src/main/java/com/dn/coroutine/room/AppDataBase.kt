package com.dn.coroutine.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dn.coroutine.room.dao.UserDao
import com.dn.coroutine.room.model.User

/**
 * @author zhangshuai
 * @date 2022/4/22 星期五
 * @email zhangshuai@dushu365.com
 * @description
 */
@Database(entities = [User::class], version = 1, exportSchema = true)
abstract class AppDataBase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        private var instance: AppDataBase? = null
        fun getInstance(context: Context): AppDataBase {
            return instance ?: synchronized(this) {
                Room.databaseBuilder(context, AppDataBase::class.java, "user.db")
                    .addMigrations()
                    .build()
                    .also {
                    instance = it
                }
            }
        }
    }

}