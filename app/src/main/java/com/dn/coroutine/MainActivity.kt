package com.dn.coroutine

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
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
        
        var i=10
        val n=++i%5

        Log.i("print_logs", "MainActivity::onCreate: i= $i, n= $n")

        val m=456
        val a=m%10
        val b=m/10
        val c=(m-m/100*100)%10
        Log.i("print_logs", "MainActivity::onCreate: a=$a, b=$b, c=$c")


    }
}