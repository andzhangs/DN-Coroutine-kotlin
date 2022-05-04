package com.project.coroutines.mapper

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */
interface Mapper<I, O> {
    fun map(input: I): O
}