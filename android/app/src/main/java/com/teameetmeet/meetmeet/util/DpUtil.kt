package com.teameetmeet.meetmeet.util

import android.content.Context
import kotlin.math.roundToInt

fun convertDpToPx(context: Context, dp: Int) : Int {
    val density: Float = context.resources.displayMetrics.density
    return (dp.toFloat() * density).roundToInt()
}