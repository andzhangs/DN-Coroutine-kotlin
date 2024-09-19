package com.dn.coroutine.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import com.dn.coroutine.databinding.FragmentDownloadBinding
import com.dn.coroutine.download.DownloadManager
import com.dn.coroutine.download.DownloadStatus
import kotlinx.coroutines.launch
import java.io.File

class DownloadFragment : Fragment() {

        private val url="https://www.wanandroid.com/blogimgs/2f859d26-e80a-4f08-a62a-f1c8236333cf.png"
//    private val url = "https://cn.bing.com/th?id=OHR.IcelandicSummer_ZH-CN1779278033_1920x1080.jpg&rf=LaDigue_1920x1080.jpg"

    private val mBinding: FragmentDownloadBinding by lazy {
        FragmentDownloadBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return mBinding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        context?.apply {
            lifecycleScope.launch {
                val file = File(
                    getExternalFilesDir(null)?.path,
                    "2f859d26-e80a-4f08-a62a-f1c8236333cf.png"
                )
                DownloadManager.download(url, file)
                    .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                    .collect {
                        when (it) {
                            is DownloadStatus.Progress -> {
                                mBinding.apply {
                                    progressBar.progress = it.value
                                    acTvPercent.text = "${it.value}%"
                                }
                            }
                            is DownloadStatus.Done -> {
                                mBinding.apply {
                                    progressBar.progress = 100
                                    acTvPercent.text = "100%"
                                }
                                Toast.makeText(context, "下载完成", Toast.LENGTH_SHORT).show()
                            }
                            is DownloadStatus.Error -> {
                                Toast.makeText(context, "下载错误：${it.throwable}", Toast.LENGTH_SHORT).show()
                                Log.e("print_logs", "DownloadFragment::onViewCreated: $it")
                            }
                            else -> {
                                Toast.makeText(context, "下载失败！", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
            }
        }
    }

}