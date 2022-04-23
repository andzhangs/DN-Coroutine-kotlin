package com.dn.coroutine.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.dn.coroutine.adapter.UserListAdapter
import com.dn.coroutine.databinding.FragmentRoomBinding
import com.dn.coroutine.room.UserViewModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collect


class RoomFragment : Fragment() {

    private val mBinding: FragmentRoomBinding by lazy { FragmentRoomBinding.inflate(layoutInflater) }

    private val mUserViewModel by viewModels<UserViewModel>()
    private lateinit var mAdapter : UserListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.let {
            mBinding.recyclerView.layoutManager = LinearLayoutManager(it)
            mAdapter= UserListAdapter(it)
            mBinding.recyclerView.adapter = mAdapter
            lifecycleScope.launchWhenCreated {
                mUserViewModel.getAll().collect { list ->
                    Log.i("print_logs", "RoomFragment::onViewCreated: 查询所有")
                    mAdapter.setData(list)
                }
            }
        }


        mBinding.apply {
            acBtnAddUser.setOnClickListener {
                if (acEtUserId.text.toString().isEmpty()) {
                    Toast.makeText(context, "请输入用户Id", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                mUserViewModel.insert(
                    acEtUserId.text.toString(),
                    acEtFirstName.text.toString(),
                    acEtLastName.text.toString()
                )
            }

            acBtnQueryUser.setOnClickListener {
                if (acEtUserId.text.toString().isEmpty()) {
                    Toast.makeText(context, "请输入用户Id", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                lifecycleScope.launchWhenCreated(){
                    mUserViewModel.getUserById(acEtUserId.text.toString()).collect { user ->
                        Log.i("print_logs", "通过ID查询: $user")
                        mAdapter.setData(listOf(user))
                        cancel()
                    }
                }
            }
        }
    }
}