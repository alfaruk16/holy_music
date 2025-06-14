package com.holymusic.app.core.exoplayer

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.widget.Toast
import androidx.media.MediaBrowserServiceCompat
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.ext.mediasession.MediaSessionConnector
import com.google.android.exoplayer2.ext.mediasession.TimelineQueueNavigator
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.holymusic.app.core.exoplayer.callbacks.MusicPlaybackPreparer
import com.holymusic.app.core.exoplayer.callbacks.MusicPlayerEventListener
import com.holymusic.app.core.exoplayer.callbacks.MusicPlayerNotificationListener
import com.holymusic.app.core.exoplayer.other.Constants.MEDIA_ROOT_ID
import com.holymusic.app.core.exoplayer.other.Constants.NETWORK_ERROR
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SERVICE_TAG = "MusicService"

@AndroidEntryPoint
class MusicService : MediaBrowserServiceCompat() {

    @Inject
    lateinit var dataSourceFactory: DefaultDataSourceFactory

    @Inject
    lateinit var exoPlayer: SimpleExoPlayer

    @Inject
    lateinit var musicSource: MusicSource

    private lateinit var musicNotificationManager: MusicNotificationManager

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)

    private lateinit var mediaSession: MediaSessionCompat
    private lateinit var mediaSessionConnector: MediaSessionConnector

    var isForegroundService = false

    private var curPlayingSong: MediaMetadataCompat? = null

    private var isPlayerInitialized = false

    private lateinit var musicPlayerEventListener: MusicPlayerEventListener

    companion object {
        var curSongDuration = 0L
            private set
        var showedNotification = false
        var played = false
    }

    override fun onCreate() {
        val context = this
        super.onCreate()
        serviceScope.launch {
            try {
                musicSource.fetchMediaData().let {

                    val activityIntent =
                        packageManager?.getLaunchIntentForPackage(packageName)?.let {
                            PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_IMMUTABLE)
                        }

                    mediaSession = MediaSessionCompat(context, SERVICE_TAG).apply {
                        setSessionActivity(activityIntent)
                        isActive = true
                    }

                    sessionToken = mediaSession.sessionToken

                    musicNotificationManager = MusicNotificationManager(
                        context,
                        mediaSession.sessionToken,
                        MusicPlayerNotificationListener(context)
                    ) {
                        curSongDuration = exoPlayer.duration
                    }

                    val musicPlaybackPreparer = MusicPlaybackPreparer(musicSource) {
                        curPlayingSong = it
                        preparePlayer(
                            musicSource.songs,
                            it,
                            true
                        )
                    }

                    mediaSessionConnector = MediaSessionConnector(mediaSession)
                    mediaSessionConnector.setPlaybackPreparer(musicPlaybackPreparer)
                    mediaSessionConnector.setQueueNavigator(MusicQueueNavigator())
                    mediaSessionConnector.setPlayer(exoPlayer)

                    musicPlayerEventListener = MusicPlayerEventListener(context)
                    exoPlayer.addListener(musicPlayerEventListener)

                    exoPlayer.addListener(object : Player.Listener {

                        override fun onEvents(player: Player, events: Player.Events) {
                            super.onEvents(player, events)
                            if(played && !showedNotification){
                                musicNotificationManager.showNotification(exoPlayer)
                            }
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onPlayerStateChanged(
                            playWhenReady: Boolean,
                            playbackState: Int
                        ) {
                            when (playbackState) {
                                Player.STATE_IDLE -> {}
                                Player.STATE_BUFFERING -> {}
                                Player.STATE_READY -> {
                                    curSongDuration = exoPlayer.duration
                                }

                                Player.STATE_ENDED -> {}
                            }
                        }
                    })
                    //musicNotificationManager.showNotification(exoPlayer)
                }
            } catch (e: Exception) {
                println(e.message)
            }
        }
    }

    private inner class MusicQueueNavigator : TimelineQueueNavigator(mediaSession) {
        override fun getMediaDescription(player: Player, windowIndex: Int): MediaDescriptionCompat {
            return musicSource.songs[windowIndex].description
        }
    }

    private fun preparePlayer(
        songs: List<MediaMetadataCompat>,
        itemToPlay: MediaMetadataCompat?,
        playNow: Boolean
    ) {
        val curSongIndex = if (curPlayingSong == null) 0 else songs.indexOf(itemToPlay)
        exoPlayer.prepare(musicSource.asMediaSource(dataSourceFactory))
        exoPlayer.seekTo(curSongIndex, 0L)
        exoPlayer.playWhenReady = playNow
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        exoPlayer.stop()
        serviceScope.cancel()
        stopSelf()
        android.os.Process.killProcess(android.os.Process.myPid())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return Service.START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
       try {
           exoPlayer.release()
           serviceScope.cancel()
           exoPlayer.removeListener(musicPlayerEventListener)
       }catch (e: Exception){
           println(e.message)
       }
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {
        return BrowserRoot(MEDIA_ROOT_ID, null)
    }

    override fun onLoadChildren(
        parentId: String,
        result: Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        when (parentId) {
            MEDIA_ROOT_ID -> {
                val resultsSent = musicSource.whenReady { isInitialized ->
                    if (isInitialized) {
                        result.sendResult(musicSource.asMediaItems())
                        if (!isPlayerInitialized && musicSource.songs.isNotEmpty()) {
                            preparePlayer(musicSource.songs, musicSource.songs[0], false)
                            isPlayerInitialized = true
                        }
                    } else {
                        mediaSession.sendSessionEvent(NETWORK_ERROR, null)
                        result.sendResult(null)
                    }
                }
                if (!resultsSent) {
                    result.detach()
                }
            }
        }
    }

}























