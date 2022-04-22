package com.dn.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.dn.coroutine.databinding.ActivityMainBinding
import java.security.MessageDigest

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