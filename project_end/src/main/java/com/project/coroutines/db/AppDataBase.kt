package com.project.coroutines.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.project.coroutines.db.dao.ProjectDao
import com.project.coroutines.db.entity.ProjectEntity

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
@Database(
    entities = [ProjectEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDataBase : RoomDatabase() {

    abstract fun getProjectDao(): ProjectDao

}