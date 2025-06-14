package com.holymusic.app.features.domain.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.holymusic.app.MainActivity
import com.holymusic.app.R
import com.holymusic.app.core.util.AppConstants
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val service = CoroutineScope(Dispatchers.IO)
    companion object {
        const val notificationChannelId = "notificationChannelId"
        const val notificationId = 1
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        // Handle data payload of FCM messages.
        if (message.data.isNotEmpty()) {
            println("message: ${message.data}")
        }

        // Handle notification payload of FCM messages.
        message.notification?.let {
            println("notification: " + it.body)
        }

        val map = message.data
        val deepLink = map[AppConstants.deepLinkScreen]
        val deepLinkId = map[AppConstants.deepLinkId]
        val image = map[AppConstants.image]
        val tittle = message.notification?.title ?: ""
        val text = message.notification?.body ?: ""

        val notification = NotificationCompat.Builder(
            this,
            notificationChannelId
        )
            .setSmallIcon(R.drawable.logo)
            .setContentTitle(tittle)
            .setContentText(text)
            .setContentIntent(
                PendingIntent.getActivities(
                    this,
                    notificationId,
                    arrayOf(
                        Intent(this, MainActivity::class.java).putExtra(
                            AppConstants.deepLinkScreen,
                            deepLink ?: ""
                        ).putExtra(AppConstants.deepLinkId, deepLinkId)
                    ),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                )
            )

        try {
            val futureTarget = Glide.with(this)
                .asBitmap()
                .load(image)
                .submit()
            val bitmap = futureTarget.get()
            notification.setLargeIcon(bitmap)
        } catch (e: Exception) {
            println(e.message)
        }

        val notificationBuilder = notification.build()
        notificationBuilder.flags = Notification.FLAG_AUTO_CANCEL

        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notificationBuilder)

    }
}