package com.flow.lsn3

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.flow.lsn3.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var mDataBinding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mDataBinding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        mDataBinding.lifecycleOwner=this

        lifecycleScope.launch {
            FlowApplication.getInstance().stateFlow.collect{
                mDataBinding.showText=it.toString()
            }
        }

        mDataBinding.acBtnSend.setOnClickListener {
            lifecycleScope.launch {
                FlowApplication.getInstance().stateFlow.emit(System.currentTimeMillis().toInt())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mDataBinding.unbind()
    }
}