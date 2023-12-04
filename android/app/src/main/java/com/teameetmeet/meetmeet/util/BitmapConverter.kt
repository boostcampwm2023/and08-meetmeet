package com.teameetmeet.meetmeet.util

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide
import com.teameetmeet.meetmeet.R

fun String.toBitmap(context: Context): Bitmap {
    return Glide.with(context).asBitmap().placeholder(R.drawable.ic_person).load(this).submit()
        .get()
}