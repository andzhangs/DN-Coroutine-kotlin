package com.dn.coroutine.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.dn.coroutine.R
import com.dn.coroutine.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private val mBinding: FragmentHomeBinding by lazy { FragmentHomeBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.acBtnFlowAndDownload.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_downloadFragment)
        }

        mBinding.acBtnFlowAndRoom.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_roomFragment)
        }


    }


}