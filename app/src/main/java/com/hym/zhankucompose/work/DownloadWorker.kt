package com.hym.zhankucompose.work

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerParameters
import com.hym.zhankucompose.R
import com.hym.zhankucompose.util.PictureUtils
import java.util.concurrent.TimeUnit

/**
 * @author hehua2008
 * @date 2022/3/24
 */
class DownloadWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {
    companion object {
        private const val KEY_URLS = "KEY_URLS"
        private const val CHANNEL_ID = "ZhanKu Download Channel"
        private const val CHANNEL_NAME = "ZhanKu Download Worker"
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                ContextCompat.getSystemService(
                    applicationContext,
                    NotificationManager::class.java
                )!!
            var notificationChannel: NotificationChannel? =
                notificationManager.getNotificationChannel(CHANNEL_ID)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        return NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setAutoCancel(false)
            .build()
    }

    override suspend fun doWork(): Result {
        val imgUrls: Array<String> = inputData.getStringArray(KEY_URLS) ?: return Result.success()
        // TODO: Fix show Snackbar
        val failedUrls = PictureUtils.coroutineDownload(null, *imgUrls)
        return Result.success()
    }
}
