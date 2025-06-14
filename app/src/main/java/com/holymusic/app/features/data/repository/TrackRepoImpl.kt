package com.holymusic.app.features.data.repository

import com.holymusic.app.core.di.ApplicationScope
import com.holymusic.app.core.di.DefaultDispatcher
import com.holymusic.app.features.data.local.TrackDao
import com.holymusic.app.features.data.local.toExternal
import com.holymusic.app.features.data.local.toLocal
import com.holymusic.app.features.data.remote.model.TracksDtoItem
import com.holymusic.app.features.domain.repository.TrackRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepoImpl @Inject constructor(
    private val trackDao: TrackDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
): TrackRepo {
    override suspend fun getTracks(): List<TracksDtoItem> {
        return withContext(dispatcher){
            trackDao.getAll().toExternal()
        }
    }

    override suspend fun getTrackByType(type: String, limit: Int, offset: Int): List<TracksDtoItem> {
        return withContext(dispatcher){
            trackDao.getAllByType(type, limit = limit, offset = offset).toExternal()
        }
    }

    override suspend fun getTrack(trackId: String): TracksDtoItem? {
        return trackDao.getById(trackId)?.toExternal()
    }

    override suspend fun createTrack(track: TracksDtoItem): String {
        trackDao.upsert(track.toLocal())
        return track.id ?: ""
    }

    override suspend fun createTracks(tracks: List<TracksDtoItem>) {
        trackDao.upsertAll(tracks.toLocal())
    }

    override suspend fun deleteTrack(trackId: String) {
        trackDao.deleteById(trackId)
    }

    override suspend fun deleteAllTracks() {
        trackDao.deleteAll()
    }
}