package com.jetpacket.paging.adapter

import android.graphics.Color
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.jetpacket.paging.R
import com.squareup.picasso.Picasso

/**
 * @author zhangshuai
 * @date 2022/5/3 星期二
 * @email zhangshuai@dushu365.com
 * @description
 */
class ImageViewBindingAdapter {

    companion object {
        @JvmStatic
        @BindingAdapter("image")
        fun setNetworkImage(imageView: ImageView, url: String) {
            if (url.isNotEmpty()) {
                Picasso.get().load(url).placeholder(R.mipmap.ic_launcher).into(imageView)
            } else {
                imageView.setBackgroundColor(Color.GRAY)
            }
        }
    }

}