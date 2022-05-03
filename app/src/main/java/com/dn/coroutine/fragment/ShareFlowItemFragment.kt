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
import com.dn.coroutine.databinding.FragmentShareFlowItemBinding
import com.dn.coroutine.sharedflow.LocalEventBus
import com.dn.coroutine.sharedflow.SharedFlowViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class ShareFlowItemFragment : Fragment() {

    private val mBinding by lazy { FragmentShareFlowItemBinding.inflate(layoutInflater) }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            LocalEventBus.flow
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect {
                    mBinding.acTvText.text = it.timestamp.toString()
                }
        }
    }
}