package com.example.mvvmnetwork.extenstions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.example.mvvmnetwork.R

fun ImageView.loadImage(uri: String?, circleCrop: Boolean = false){
    if (circleCrop){
        Glide.with(this).load(uri).placeholder(R.drawable.placeholder_image).circleCrop().into(this)
    }else {
        Glide.with(this).load(uri).placeholder(R.drawable.placeholder_image).into(this)
    }
}