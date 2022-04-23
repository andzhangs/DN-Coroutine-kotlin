package com.dn.coroutine.retrofit.model

import com.google.gson.Gson
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

/**
 * @author zhangshuai
 * @date 2022/4/23 星期六
 * @email zhangshuai@dushu365.com
 * @description
 */
@Serializable
data class HpImageArchiveModel(
    val images: List<Image>,
    val tooltips: Tooltips
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

@Serializable
data class Image(
    val bot: Int,
    val copyright: String,
    val copyrightlink: String,
    val drk: Int,
    val enddate: String,
    val fullstartdate: String,
    val hs: List<@Contextual Any>,
    val hsh: String,
    val quiz: String,
    val startdate: String,
    val title: String,
    val top: Int,
    val url: String,
    val urlbase: String,
    val wp: Boolean
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}

@Serializable
data class Tooltips(
    val loading: String,
    val next: String,
    val previous: String,
    val walle: String,
    val walls: String
) {
    override fun toString(): String {
        return Gson().toJson(this)
    }
}