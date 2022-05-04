package com.project.coroutines.db.dao

import android.util.Log
import androidx.paging.PagingSource
import androidx.room.*
import com.project.coroutines.db.entity.ProjectEntity

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */
@Dao
interface ProjectDao {

    @Query("DELETE FROM tb_projectEntity")
    suspend fun clearAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entities: List<ProjectEntity>)

    @Transaction
    suspend fun refresh(entities: List<ProjectEntity>) {
        Log.i("print_logs", "ProjectDao::refresh")
        clearAll()
        insert(entities)
    }

    @Query("SELECT * FROM tb_projectEntity")
    fun getAll(): PagingSource<Int, ProjectEntity>
}