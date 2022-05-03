package com.dn.coroutine.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.*
import com.dn.coroutine.R
import com.dn.coroutine.databinding.FragmentShareFlowBinding
import com.dn.coroutine.sharedflow.SharedFlowViewModel
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ShareFlowFragment : Fragment() {

    private val mBinding by lazy { FragmentShareFlowBinding.inflate(layoutInflater) }

    private val mSharedViewModel by viewModels<SharedFlowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.acBtnStart.setOnClickListener {
            mSharedViewModel.start()
        }

        mBinding.acBtnStop.setOnClickListener {
            mSharedViewModel.stop()
        }


    }
}