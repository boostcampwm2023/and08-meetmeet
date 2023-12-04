package com.teameetmeet.meetmeet.presentation.model

import android.net.Uri

data class MediaItem(
    val isVideo: Boolean,
    val uri: Uri,
    val size: Long
) {
    val sizeString =
        if (size / KILO < 1) "${size}B"
        else if (size / (KILO * KILO) < 1) "${size / KILO}KB"
        else "${size / (KILO * KILO)}MB"

    companion object {
        const val KILO = 1024
        const val MEDIA_AMOUNT_CONSTRAINT = 10
        const val MEDIA_VOLUME_CONSTRAINT = 1024 * 1024 * 50
    }
}