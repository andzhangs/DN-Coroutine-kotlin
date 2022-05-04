package com.project.coroutines.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */
@Entity(tableName = "tb_projectEntity")
data class ProjectEntity(
    @PrimaryKey
    val id: Int,
    val author: String,
    val envelopePic: String,
    val page: Int = 0//页码
)
