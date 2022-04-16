package com.zs.coroutine.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.zs.coroutine.R
import com.zs.coroutine.api.UserServiceApi
import com.zs.coroutine.databinding.ActivityMain2Binding
import com.zs.coroutine.viewmodel.MainViewModel

class MainActivity2 : AppCompatActivity() {

    private lateinit var mBinding: ActivityMain2Binding
    private val mainViewModel :MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main2)
        mBinding.viewModel = mainViewModel
        mBinding.lifecycleOwner=this
        clickInit()
    }

    private fun clickInit(){
        mBinding.acBtnGetUser.setOnClickListener {
            mainViewModel.getUser(UserServiceApi.USER_NAME)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding.unbind()
    }
}