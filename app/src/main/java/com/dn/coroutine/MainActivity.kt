package com.dn.coroutine

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.dn.coroutine.databinding.ActivityMainBinding
import java.security.MessageDigest

class MainActivity : AppCompatActivity() {

    external fun stringFromJNI(): String

    companion object {
        init {
            System.loadLibrary("coroutine")
        }
    }
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.sampleText.text = stringFromJNI()
        MessageDigest.getInstance("SHA-1").digest()
    }




}