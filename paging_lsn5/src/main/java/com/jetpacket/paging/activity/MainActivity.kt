package com.jetpacket.paging.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.jetpacket.paging.adapter.LoadMoreAdapter
import com.jetpacket.paging.adapter.ProjectListAdapter
import com.jetpacket.paging.databinding.ActivityMainBinding
import com.jetpacket.paging.viewmodel.ProjectListViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val mViewModel by viewModels<ProjectListViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
        init()
    }

    private fun init() {
        val projectAdapter = ProjectListAdapter()
        mBinding.recyclerview.apply {
            //设置适配器，并自定义加载更多
            adapter = projectAdapter.withLoadStateFooter(LoadMoreAdapter(this@MainActivity))
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, RecyclerView.VERTICAL))
        }
        lifecycleScope.launch {
            mViewModel.loadList()
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectLatest {
                    projectAdapter.submitData(it)
                }
        }
        lifecycleScope.launch {
            projectAdapter.loadStateFlow
                .flowWithLifecycle(lifecycle,Lifecycle.State.STARTED)
                .collectLatest {
                    Log.e("print_logs", "MainActivity::init: ${it.refresh.endOfPaginationReached}")
                    mBinding.swipeRefreshLayout.isRefreshing = it.refresh is LoadState.Loading
                }
        }


        //刷新
        mBinding.swipeRefreshLayout.setOnRefreshListener {
//            projectAdapter.retry()
            projectAdapter.refresh()
        }
    }
}