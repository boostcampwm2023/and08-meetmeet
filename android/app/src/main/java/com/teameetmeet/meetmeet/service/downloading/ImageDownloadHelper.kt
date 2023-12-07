package com.teameetmeet.meetmeet.service.downloading

import android.content.Context
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.teameetmeet.meetmeet.data.model.Content
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ImageDownloadHelper @Inject constructor(
    private val context: Context
) {
    private val workManager =  WorkManager.getInstance(context)

    fun saveImage(content: Content, type: String): Flow<List<WorkInfo>> {
        val data = Data.Builder()
            .putString(ImageDownloadWorker.KEY_IMAGE_URL, content.path)
            .putString(ImageDownloadWorker.KEY_MIME_TYPE, content.mimeType)
            .build()

        val constraints = Constraints.Builder().setRequiresStorageNotLow(true).build()
        val workRequest = OneTimeWorkRequestBuilder<ImageDownloadWorker>()
            .setConstraints(constraints)
            .addTag(ImageDownloadWorker.TAG_WORK_INFO)
            .addTag(type)
            .setInputData(data)
            .build()
        workManager.enqueue(workRequest)
        return workManager.getWorkInfosByTagFlow(ImageDownloadWorker.TAG_WORK_INFO)
    }
}