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
        val result = getFileUri(
            imageUrl,
            "${context.getString(R.string.common_app_name)}-${
                getLocalDateTime().toLong().toDateString(DateTimeFormat.ISO_DATE_TIME)
            }"
        )
        return if (result != null) {
            Result.success()
        } else {
            Result.failure()
        }
    }

    private fun getFileUri(
        imageUrl: String,
        imageName: String
    ): String? {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val mimeType = getMimeTypeOf(imageUrl)
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, imageName)
                    put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        when(mimeType) {
                            VIDEO_MIME_TYPE ->{
                                "${Environment.DIRECTORY_MOVIES}/${context.getString(R.string.common_app_name)}"
                            }
                            else -> {
                                "${Environment.DIRECTORY_PICTURES}/${context.getString(R.string.common_app_name)}"
                            }
                        }

                    )
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(
                    when(mimeType) {
                        VIDEO_MIME_TYPE ->{
                            MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                        }
                        else -> {
                            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
                        }
                    },
                    contentValues
                )
                MediaStore.Video.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)

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
                val extension = getExtensionOf(imageUrl)
                val uri = Uri.parse(imageUrl)
                val request = DownloadManager.Request(uri)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_MOVIES, "${context.getString(R.string.common_app_name)}/${imageName}.${extension}")
                val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                downloadManager.enqueue(request)
                return TYPE_DOWNLOAD_MANAGER
            }
        } catch (e: Exception) {
            println(e.toString())
            return null
        }
    }

    private fun getMimeTypeOf(imageUrl: String): String {
        return when(getExtensionOf(imageUrl)) {
            EXTENSION_VIDEO -> {
                VIDEO_MIME_TYPE
            }
            EXTENSION_GIF -> {
                IMAGE_MIME_TYPE_GIF
            }
            EXTENSION_JPEG -> {
                IMAGE_MIME_TYPE_JPEG
            }
            else -> {
                IMAGE_MIME_TYPE_PNG
            }
        }
    }



    private fun getExtensionOf(imageUrl: String) : String {
        return when(imageUrl.drop(imageUrl.lastIndexOf('.')+1)) {
            "mp4","MP4","MOV","mov", "MPEG", "mpeg" -> {
                EXTENSION_VIDEO
            }
            "gif" -> {
                EXTENSION_GIF
            }
            "jpg", "jpeg", "JPG", "JPEG" -> {
                EXTENSION_JPEG
            }
            else -> {
                EXTENSION_PNG
            }
        }
    }


    companion object {
        const val KEY_IMAGE_URL = "keyImageUrl"
        const val IMAGE_MIME_TYPE_PNG = "image/png"
        const val IMAGE_MIME_TYPE_GIF = "image/gif"
        const val IMAGE_MIME_TYPE_JPEG = "image/jpeg"
        const val VIDEO_MIME_TYPE = "video/mp4"
        const val EXTENSION_PNG = "png"
        const val EXTENSION_GIF = "gif"
        const val EXTENSION_JPEG = "jpeg"
        const val EXTENSION_VIDEO = "mp4"
        const val TAG_WORK_INFO = "tagWorkInfo"
        const val TYPE_DOWNLOAD_MANAGER = "typeDownloadManager"
        const val TYPE_MEDIA_STORE = "typeMediaStore"
    }
}