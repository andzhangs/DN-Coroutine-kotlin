package com.dn.coroutine.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.dn.coroutine.R
import com.dn.coroutine.databinding.FragmentStateFlowBinding
import com.dn.coroutine.stateflow.StateFlowViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class StateFlowFragment : Fragment() {

    private val mBinding by lazy { FragmentStateFlowBinding.inflate(layoutInflater) }

    private val mViewModel by viewModels<StateFlowViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.acBtnAddNum.setOnClickListener {
            mViewModel.increment()
        }

        mBinding.acBtnReduceNum.setOnClickListener {
            mViewModel.decrement()
        }

        lifecycleScope.launch {
            mViewModel.number
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    mBinding.acTvNumText.text = it.toString()
                }
        }

    }


}