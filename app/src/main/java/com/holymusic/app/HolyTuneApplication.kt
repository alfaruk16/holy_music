package com.holymusic.app

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.holymusic.app.features.domain.services.MyFirebaseMessagingService.Companion.notificationChannelId
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class HolyTuneApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelId,
                "notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.description = "Used for push notifications"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}