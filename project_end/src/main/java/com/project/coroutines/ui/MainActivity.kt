package com.project.coroutines.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.project.coroutines.databinding.ActivityMainBinding
import com.project.coroutines.databinding.LayoutProjectListItemBinding
import com.project.coroutines.model.ProjectItemModel
import com.project.coroutines.viewmodel.ProjectViewModel
import com.project.coroutines.widget.LoadMoreViewAdapter
import com.zs.coroutines.lib.base.util.BaseBindingViewHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private val mViewModel by viewModels<ProjectViewModel>()
//    private val mLoginViewModel: LoginViewModel by viewModels()

    private val mAdapter by lazy { ProjectAdapter(context = this@MainActivity) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)

        with(mBinding.recyclerView) {
            adapter = mAdapter.withLoadStateFooter(LoadMoreViewAdapter(this@MainActivity, mAdapter))
            layoutManager = StaggeredGridLayoutManager(3, RecyclerView.VERTICAL)
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    RecyclerView.VERTICAL
                )
            )
        }

        //刷新
        mBinding.swipeRefreshLayout.setOnRefreshListener {
            mAdapter.refresh()

//            mLoginViewModel.call()

        }

        //监听获取数据状态
        lifecycleScope.launch {
            mAdapter.loadStateFlow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    mBinding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                }
        }

        //获取数据
        mViewModel.liveData.observe(this) {
            mAdapter.submitData(lifecycle, it) //刷新UI，过程中activity可能销毁了，所以需要绑定
        }

    }

    companion object {
        class ProjectAdapter(private val context: Context) :
            PagingDataAdapter<ProjectItemModel, BaseBindingViewHolder>(object :
                DiffUtil.ItemCallback<ProjectItemModel>() {

                override fun areItemsTheSame(
                    oldItem: ProjectItemModel,
                    newItem: ProjectItemModel
                ): Boolean = oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ProjectItemModel,
                    newItem: ProjectItemModel
                ): Boolean = oldItem == newItem
            }) {

            override fun onCreateViewHolder(
                parent: ViewGroup,
                viewType: Int
            ): BaseBindingViewHolder {
                val binding = LayoutProjectListItemBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
                return BaseBindingViewHolder(binding)
            }

            override fun onBindViewHolder(holder: BaseBindingViewHolder, position: Int) {
                getItem(position)?.let {
                    (holder.binding as LayoutProjectListItemBinding).data = it
                }
            }
        }

    }

}