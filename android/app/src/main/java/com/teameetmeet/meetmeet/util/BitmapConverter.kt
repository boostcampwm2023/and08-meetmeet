package com.teameetmeet.meetmeet.util

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.Glide

fun String.toBitmap(context: Context): Bitmap {
    return Glide.with(context).asBitmap().load(this).submit().get()
}