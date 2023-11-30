package com.teameetmeet.meetmeet.presentation.model

import android.net.Uri

data class MediaItem(
    val isVideo: Boolean,
    val uri: Uri,
    val size: Long
) {
    private val kilo = 1024
    val sizeString =
        if (size / kilo < 1) "${size}B"
        else if (size / (kilo * kilo) < 1) "${size / kilo}KB"
        else "${size / (kilo * kilo)}MB"
}