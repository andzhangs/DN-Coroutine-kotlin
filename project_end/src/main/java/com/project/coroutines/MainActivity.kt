package com.project.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.project.coroutines.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val mBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mBinding.root)
    }
}