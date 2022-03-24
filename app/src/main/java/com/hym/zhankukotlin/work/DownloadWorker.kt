package com.hym.zhankukotlin.work

import android.app.Notification
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.*
import com.hym.zhankukotlin.util.PictureUtils
import java.util.concurrent.TimeUnit

/**
 * @author hehua2008
 * @date 2022/3/24
 */
class DownloadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val KEY_URLS = "KEY_URLS"
        private const val CHANNEL_ID = "Download Channel"
        private const val NOTIFICATION_ID = 20220324

        @JvmStatic
        fun enqueue(context: Context, imgUrls: List<String>) {
            if (imgUrls.isEmpty()) return
            enqueue(context, *imgUrls.toTypedArray())
        }

        @JvmStatic
        fun enqueue(context: Context, vararg imgUrls: String) {
            if (imgUrls.isEmpty()) return
            val downloadWorkRequest = OneTimeWorkRequest.Builder(DownloadWorker::class.java)
                .setInputData(Data.Builder().putStringArray(KEY_URLS, imgUrls).build())
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .setBackoffCriteria(
                    BackoffPolicy.LINEAR,
                    WorkRequest.MIN_BACKOFF_MILLIS,
                    TimeUnit.MILLISECONDS
                )
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresStorageNotLow(true)
                        .build()
                )
                .build()
            WorkManager.getInstance(context).enqueue(downloadWorkRequest)
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(NOTIFICATION_ID, createNotification())
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setAutoCancel(false)
            .build()
    }

    override suspend fun doWork(): Result {
        val imgUrls: Array<String> = inputData.getStringArray(KEY_URLS) ?: return Result.success()
        val failedUrls = PictureUtils.downloadCoroutine(*imgUrls)
        return Result.success()
    }
}