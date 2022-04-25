package com.dn.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dn.coroutine.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("coroutine")
        }
    }
    private val binding: ActivityMainBinding by lazy{ ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}