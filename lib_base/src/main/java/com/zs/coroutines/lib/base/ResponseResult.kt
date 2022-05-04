package com.zs.coroutines.lib.base

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * @author zhangshuai
 * @date 2022/4/17 星期日
 * @email zhangshuai@dushu365.com
 * @description
 * 引用地址：https://kotlinlang.org/docs/serialization.html#formats
 */
@Serializable
data class ResponseResult(val userName: String)

fun main() {
    val result=Json.encodeToString(ResponseResult("str"))
    println("打印result：$result")
}
