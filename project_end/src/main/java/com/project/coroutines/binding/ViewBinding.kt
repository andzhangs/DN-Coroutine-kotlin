package com.project.coroutines.binding

import android.util.Log
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.project.coroutines.R

/**
 * @author zhangshuai
 * @date 2022/5/4 星期三
 * @email zhangshuai@dushu365.com
 * @description
 */

@BindingAdapter("bindingImage")
fun bindingImage(imageView: ImageView, url: String) {
    Log.d("print_logs", "::bindingImage: $url")
    imageView.load(url)
}