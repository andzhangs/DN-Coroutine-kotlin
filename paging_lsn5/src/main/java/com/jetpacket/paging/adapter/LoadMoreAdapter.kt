package com.jetpacket.paging.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import com.jetpacket.paging.databinding.LayoutLoadMoreBinding
import com.jetpacket.paging.util.BindingViewModel

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description 加载更多
 */
class LoadMoreAdapter(private val context: Context) : LoadStateAdapter<BindingViewModel>() {

    override fun onBindViewHolder(holder: BindingViewModel, loadState: LoadState) {
        val binding=holder.binding as LayoutLoadMoreBinding
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): BindingViewModel {
        val binding = LayoutLoadMoreBinding.inflate(LayoutInflater.from(context), parent, false)
        return BindingViewModel(binding)
    }

}