package com.holymusic.app.features.domain.repository

import com.holymusic.app.features.data.remote.model.TracksDtoItem

interface TrackRepo {

    suspend fun getTracks(): List<TracksDtoItem>

    suspend fun getTrackByType(type: String, limit: Int = Int.MAX_VALUE, offset: Int = 0): List<TracksDtoItem>

    suspend fun getTrack(trackId: String): TracksDtoItem?

    suspend fun createTrack(track: TracksDtoItem): String

    suspend fun createTracks(tracks: List<TracksDtoItem>)

    suspend fun deleteTrack(trackId: String)

    suspend fun deleteAllTracks()

}