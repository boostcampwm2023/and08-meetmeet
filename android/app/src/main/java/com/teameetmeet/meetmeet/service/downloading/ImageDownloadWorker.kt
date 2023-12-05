package com.teameetmeet.meetmeet.service.downloading

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.teameetmeet.meetmeet.R
import com.teameetmeet.meetmeet.util.date.DateTimeFormat
import com.teameetmeet.meetmeet.util.date.getLocalDateTime
import com.teameetmeet.meetmeet.util.date.toDateString
import com.teameetmeet.meetmeet.util.date.toLong
import java.net.URL

class ImageDownloadWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val imageUrl = inputData.getString(KEY_IMAGE_URL) ?: return Result.failure()
        val mimeType = inputData.getString(KEY_MIME_TYPE) ?: return Result.failure()
        val uri = getFileUri(
            imageUrl,
            "${context.getString(R.string.common_app_name)}-${
                getLocalDateTime().toLong().toDateString(DateTimeFormat.ISO_DATE_TIME)
            }",
            mimeType
        )
        return if (uri != null) {
            Result.success(workDataOf(KEY_FILE_URI to uri.toString()))
        } else {
            Result.failure()
        }
    }

    private fun getFileUri(
        imageUrl: String,
        imageName: String,
        mimeType: String

    ): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
            put(MediaStore.MediaColumns.MIME_TYPE, IMAGE_MIME_TYPE)
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/${context.getString(R.string.common_app_name)}"
            )
        }
        val resolver = context.contentResolver
        try {
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                resolver.insert(
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    contentValues
                )

            } else {
                TODO("VERSION.SDK_INT < Q")
            }

            return if (uri != null) {
                URL(imageUrl).openStream().use { input ->
                    resolver.openOutputStream(uri)?.use { output ->
                        input.copyTo(output)
                    }
                }
                uri
            } else {
                null
            }

        } catch (_: Exception) {
            return null
        }
    }


    companion object {
        const val KEY_IMAGE_URL = "keyImageUrl"
        const val KEY_MIME_TYPE = "keyMimeType"
        const val KEY_FILE_URI = "keyFileUri"
        const val IMAGE_MIME_TYPE = "image/png"
        const val TAG_WORK_INFO = "tagWorkInfo"
    }
}