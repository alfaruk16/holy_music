package com.holymusic.app.core.exoplayer.callbacks

import android.annotation.SuppressLint
import android.app.Notification
import android.content.Intent
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ui.PlayerNotificationManager
import com.holymusic.app.core.exoplayer.MusicService
import com.holymusic.app.core.exoplayer.other.Constants.NOTIFICATION_ID

class MusicPlayerNotificationListener(
    private val musicService: MusicService
) : PlayerNotificationManager.NotificationListener {

    override fun onNotificationCancelled(notificationId: Int, dismissedByUser: Boolean) {
        super.onNotificationCancelled(notificationId, dismissedByUser)
        musicService.apply {
            stopForeground(true)
            isForegroundService = false
            stopSelf()
        }
    }

    @SuppressLint("ForegroundServiceType")
    override fun onNotificationPosted(
        notificationId: Int,
        notification: Notification,
        ongoing: Boolean
    ) {
        super.onNotificationPosted(notificationId, notification, ongoing)
        musicService.apply {
            if(ongoing && !isForegroundService) {
                ContextCompat.startForegroundService(
                    this,
                    Intent(applicationContext, this::class.java)
                )
                try {
                    startForeground(NOTIFICATION_ID, notification)
                }catch (e: Exception){
                    println(e.message)
                }
                isForegroundService = true
            }
        }
    }
}











