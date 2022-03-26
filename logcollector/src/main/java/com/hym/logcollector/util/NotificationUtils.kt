package com.hym.logcollector.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.hym.logcollector.R

/**
 * @author hehua2008
 * @date 2021/9/15
 */
internal object NotificationUtils {
    fun showNotification(
        context: Context,
        contentTitle: CharSequence,
        contentText: CharSequence,
        pendingIntent: PendingIntent?,
        notificationId: Int,
        channelId: String,
        channelName: String = channelId
    ) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentText(contentText)
            .setContentTitle(contentTitle)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
        val notification = buildNotification(context, builder, channelId, channelName)
        val notificationManager =
            ContextCompat.getSystemService(context, NotificationManager::class.java)!!
        notificationManager.notify(notificationId, notification)
    }

    fun buildNotification(
        context: Context,
        builder: NotificationCompat.Builder,
        channelId: String,
        channelName: String = channelId
    ): Notification {
        builder.setSmallIcon(R.drawable.ic_collect).setWhen(System.currentTimeMillis())
        builder.setChannelId(channelId)
        builder.setGroup(channelId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                ContextCompat.getSystemService(context, NotificationManager::class.java)!!
            var notificationChannel: NotificationChannel? =
                notificationManager.getNotificationChannel(channelId)
            if (notificationChannel == null) {
                notificationChannel = NotificationChannel(
                    channelId,
                    channelName,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        return builder.build()
    }
}