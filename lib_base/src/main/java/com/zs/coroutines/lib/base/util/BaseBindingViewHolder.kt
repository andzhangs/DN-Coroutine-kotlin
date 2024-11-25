package com.zs.coroutines.lib.base.util

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
open class BaseBindingViewHolder(val binding: ViewBinding) : RecyclerView.ViewHolder(binding.root)
