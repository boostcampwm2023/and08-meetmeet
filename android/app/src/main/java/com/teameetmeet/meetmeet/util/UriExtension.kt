package com.teameetmeet.meetmeet.util

import android.net.Uri
import android.provider.OpenableColumns
import com.teameetmeet.meetmeet.MeetMeetApp
import java.io.File
import java.io.FileOutputStream

fun Uri.toAbsolutePath(): String? {
    val context = MeetMeetApp.applicationContext()
    val contentResolver = context.contentResolver
    var fileName: String? = null

    contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        fileName = cursor.getString(nameIndex)
    }

    val file = fileName?.let {
        val media = File(context.filesDir, "media")
        media.mkdirs()
        File(media, it)
    }

    contentResolver.openInputStream(this).use { input ->
        FileOutputStream(file).use { output ->
            input?.copyTo(output)
        }
    }
    return file?.absolutePath
}

fun Uri.getMimeType(): String? {
    val context = MeetMeetApp.applicationContext()
    val contentResolver = context.contentResolver
    return contentResolver.getType(this)
}

fun Uri.getSize(): Long {
    val context = MeetMeetApp.applicationContext()
    val contentResolver = context.contentResolver

    return contentResolver.query(this, null, null, null, null)?.use { cursor ->
        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
        cursor.moveToFirst()
        val size = cursor.getLong(sizeIndex).also { cursor.close() }
        size
    } ?: 0L
}