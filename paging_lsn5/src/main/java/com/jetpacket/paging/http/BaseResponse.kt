package com.jetpacket.paging.http

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
@kotlinx.serialization.Serializable
class BaseResponse<T> {
    var data: T? = null
    var errorCode: Int = 0
    var errorMsg: String = ""
}
