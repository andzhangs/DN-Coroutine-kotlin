package com.dn.coroutine.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dn.coroutine.databinding.LayoutUserListItemBinding
import com.dn.coroutine.room.model.User
import com.zs.coroutines.lib.base.util.BaseBindingViewHolder

/**
 * @author zhangshuai
 * @date 2022/4/22 星期五
 * @email zhangshuai@dushu365.com
 * @description
 */
open class UserListAdapter(private val context: Context) :
    RecyclerView.Adapter<BaseBindingViewHolder>() {

    private val mList = ArrayList<User>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<User>) {
        this.mList.clear()
        this.mList.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseBindingViewHolder {
        return BaseBindingViewHolder(
            LayoutUserListItemBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {
        val data = mList[position]
        (holder.binding as LayoutUserListItemBinding).apply {
            itemAcTvUserId.text = data.userId.toString()
            itemAcTvFirstName.text = data.firstName
            itemAcTvLastName.text = data.lastName
        }
    }

    override fun getItemCount(): Int = mList.size
}