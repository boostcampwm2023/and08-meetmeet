package com.teameetmeet.meetmeet.service.downloading

import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
        val result = getFileUri(
            imageUrl,
            "${context.getString(R.string.common_app_name)}-${
                getLocalDateTime().toLong().toDateString(DateTimeFormat.ISO_DATE_TIME)
            }",
            mimeType
        )
        return if (result != null) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun getFileUri(
        imageUrl: String,
        imageName: String,
        mimeType: String

    ): String? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, IMAGE_MIME_TYPE)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_PICTURES}/${context.getString(R.string.common_app_name)}"
                    )
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(
                    MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY),
                    contentValues
                )

                return if (uri != null) {
                    URL(imageUrl).openStream().use { input ->
                        resolver.openOutputStream(uri)?.use { output ->
                            input.copyTo(output)
                        }
                    }
                    TYPE_MEDIA_STORE
                } else {
                    null
                }
            } else {
                val uri = Uri.parse(imageUrl)
                val request = DownloadManager.Request(uri)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, "${context.getString(R.string.common_app_name)}/${imageName}.png")
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)
                return TYPE_DOWNLOAD_MANAGER
            }
        } catch (e: Exception) {
            println(e.toString())
            return null
        }
    }


    companion object {
        const val KEY_IMAGE_URL = "keyImageUrl"
        const val KEY_MIME_TYPE = "keyMimeType"
        const val KEY_DOWNLOAD_TYPE = "keyDownloadType"
        const val IMAGE_MIME_TYPE = "image/png"
        const val TAG_WORK_INFO = "tagWorkInfo"
        const val TYPE_DOWNLOAD_MANAGER = "typeDownloadManager"
        const val TYPE_MEDIA_STORE = "typeMediaStore"
    }
}