package com.dn.coroutine.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.dn.coroutine.databinding.FragmentDownloadBinding
import com.dn.coroutine.download.DownloadManager
import com.dn.coroutine.download.DownloadStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.io.File

class DownloadFragment : Fragment() {

//    private val url="https://www.wanandroid.com/blogimgs/d7bbe689-7bab-4db4-938f-24d5e4854302.png"
    private val url="https://cn.bing.com/th?id=OHR.IcelandicSummer_ZH-CN1779278033_1920x1080.jpg&rf=LaDigue_1920x1080.jpg"

    private val mBinding:FragmentDownloadBinding by lazy { FragmentDownloadBinding.inflate(layoutInflater) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return mBinding.root
    }


    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.apply {
            lifecycleScope.launch {
                val file=File(getExternalFilesDir(null)?.path,"d7bbe689-7bab-4db4-938f-24d5e4854302.png")
                DownloadManager.download(url,file).collect {
                    when (it){
                        is DownloadStatus.Progress->{
                            mBinding.apply {
                                progressBar.progress=it.value
                                acTvPercent.text="${it.value}%"
                            }
                        }
                        is DownloadStatus.Done->{
                            mBinding.apply {
                                progressBar.progress=100
                                acTvPercent.text="100%"
                            }
                            Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show()
                        }
                        is DownloadStatus.Error->{
                            Toast.makeText(context,"下载错误：${it.throwable}",Toast.LENGTH_SHORT).show()
                        }else->{
                        Toast.makeText(context,"下载失败！",Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

}