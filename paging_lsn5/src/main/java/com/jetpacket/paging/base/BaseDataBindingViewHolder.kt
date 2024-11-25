package com.jetpacket.paging.base

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.zs.coroutines.lib.base.util.BaseBindingViewHolder

/**
 *
 * @author zhangshuai
 * @date 2024/11/25 15:06
 * @description 自定义类描述
 */
class BaseDataBindingViewHolder<VB : ViewDataBinding>(private val db: VB) :
    BaseBindingViewHolder(db) {
    companion object {
        @JvmStatic
        fun <VB : ViewDataBinding> createInstance(
            @LayoutRes layoutRes: Int,
            viewGroup: ViewGroup,
            attachToRoot: Boolean = false
        ): BaseDataBindingViewHolder<VB> {

            val db = DataBindingUtil.inflate<VB>(
                LayoutInflater.from(viewGroup.context),
                layoutRes,
                viewGroup,
                attachToRoot
            )

            return BaseDataBindingViewHolder(db)
        }
    }

    val mBinding by lazy { db }
}