package com.jetpacket.paging.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.jetpacket.paging.databinding.LayoutProjectListItemBinding
import com.jetpacket.paging.model.DataX
import com.zs.coroutines.lib.base.util.BaseBindingViewHolder

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
class ProjectListAdapter(private val context: Context) :
    PagingDataAdapter<DataX, BaseBindingViewHolder>(object :
        DiffUtil.ItemCallback<DataX>() {

        override fun areItemsTheSame(oldItem: DataX, newItem: DataX): Boolean =
            oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: DataX, newItem: DataX): Boolean =
            oldItem == newItem

    }) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        val itemViewBinding =
            LayoutProjectListItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return BaseBindingViewHolder(itemViewBinding)
    }


    override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {
        val data = getItem(position)
        data?.let {
            val binding = holder.binding as LayoutProjectListItemBinding
            binding.data = it
            binding.networkImage = it.envelopePic
        }
    }
}