package com.jetpacket.paging.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.jetpacket.paging.databinding.LayoutLoadMoreBinding
import com.zs.coroutines.lib.base.util.BaseBindingViewHolder

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description 加载更多
 */
class LoadMoreAdapter(private val context: Context) : LoadStateAdapter<BaseBindingViewHolder>() {

    override fun onBindViewHolder(holder: BaseBindingViewHolder, loadState: LoadState) {
        val binding = holder.binding as LayoutLoadMoreBinding
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BaseBindingViewHolder {
        val binding = LayoutLoadMoreBinding.inflate(LayoutInflater.from(context), parent, false)
        return BaseBindingViewHolder(binding)
    }

}