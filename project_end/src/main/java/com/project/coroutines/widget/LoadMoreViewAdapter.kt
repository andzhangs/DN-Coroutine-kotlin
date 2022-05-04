package com.project.coroutines.widget

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.project.coroutines.databinding.LayoutLoadMoreViewBinding
import com.project.coroutines.ui.MainActivity
import com.zs.coroutines.lib.base.util.BaseBindingViewHolder

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description 加载更多
 */
class LoadMoreViewAdapter(
    private val context: Context,
    private val adapter: MainActivity.Companion.ProjectAdapter
) : LoadStateAdapter<BaseBindingViewHolder>() {

    override fun onBindViewHolder(holder: BaseBindingViewHolder, loadState: LoadState) {
        //水平居中
        val params=holder.itemView.layoutParams
        if (params is StaggeredGridLayoutManager.LayoutParams){
            params.isFullSpan=true
        }

        val binding = holder.binding as LayoutLoadMoreViewBinding
        when (loadState) {
            //正在加载，显示进度条
            is LoadState.Loading -> {
                binding.progress.isVisible
            }
            //加载失败
            is LoadState.Error -> {
                binding.progress.isVisible = false
                binding.actvLoading.apply {
                    text = "点击重试"
                    setOnClickListener {
                        adapter.retry()
                    }
                    Log.e(
                        "print_logs",
                        "LoadMoreViewAdapter::onBindViewHolder::加载失败 ${loadState.error.message}"
                    )
                }
            }
            else -> {}
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        loadState: LoadState
    ): BaseBindingViewHolder {
        val binding = LayoutLoadMoreViewBinding.inflate(LayoutInflater.from(context), parent, false)
        return BaseBindingViewHolder(binding)
    }
}

inline var View.isVisible: Boolean
    get() = visibility == View.VISIBLE
    set(value) {
        visibility = if (value) View.VISIBLE else View.GONE
    }