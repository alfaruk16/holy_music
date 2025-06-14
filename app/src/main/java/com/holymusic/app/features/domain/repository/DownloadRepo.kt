package com.holymusic.app.features.domain.repository

import com.holymusic.app.features.data.local.model.Download
import kotlinx.coroutines.flow.Flow

interface DownloadRepo {

    fun getTracksStream(): Flow<List<Download>>

    suspend fun getTracks(): List<Download>

    fun getTrackStream(trackId: String): Flow<Download>

    suspend fun getTrack(trackId: String): Download?

    suspend fun isDownloaded(trackId: String): Boolean

    suspend fun createTrack(track: Download): String

    suspend fun updateTrack(trackId: String, track: Download)

    suspend fun completeTrack(trackId: String)

    suspend fun deleteTrack(trackId: String)

    suspend fun deleteAllTracks()

}