package com.holymusic.app.features.data.repository

import com.holymusic.app.core.di.ApplicationScope
import com.holymusic.app.core.di.DefaultDispatcher
import com.holymusic.app.features.data.local.DownloadDao
import com.holymusic.app.features.data.local.model.Download
import com.holymusic.app.features.data.local.toExternal
import com.holymusic.app.features.data.local.toLocal
import com.holymusic.app.features.domain.repository.DownloadRepo
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepoImpl @Inject constructor(
    private val downloadDao: DownloadDao,
    @DefaultDispatcher private val dispatcher: CoroutineDispatcher,
    @ApplicationScope private val scope: CoroutineScope
) : DownloadRepo {
    override fun getTracksStream(): Flow<List<Download>> {
        return downloadDao.observeAll().map { tracks ->
            withContext(dispatcher) {
                tracks.toExternal()
            }
        }
    }

    override suspend fun getTracks(): List<Download> {
        return withContext(dispatcher) {
            downloadDao.getAll().toExternal()
        }
    }

    override fun getTrackStream(trackId: String): Flow<Download> {
        return downloadDao.observeById(trackId).map { it.toExternal() }
    }

    override suspend fun getTrack(trackId: String): Download? {
        return downloadDao.getById(trackId)?.toExternal()
    }

    override suspend fun isDownloaded(trackId: String): Boolean {
        try {
            return downloadDao.isDownloaded(trackId)
        } catch (e: Exception) {
            println(e.message)
        }
        return false
    }

    override suspend fun createTrack(track: Download): String {
        try {
            downloadDao.upsert(track.toLocal())
        } catch (e: Exception) {
            println(e.message)
        }
        return track.id
    }

    override suspend fun updateTrack(trackId: String, track: Download) {
        val data = getTrack(trackId)?.copy(
            title = track.title,
            artistName = track.artistName,
            description = track.description,
            localPath = track.localPath,
            imageUrl = track.imageUrl,
            duration = track.duration,
            percentage = track.percentage,
            isComplete = track.isComplete
        ) ?: throw Exception("Track (id $trackId) not found")
        downloadDao.upsert(data.toLocal())
    }

    override suspend fun completeTrack(trackId: String) {
        val track = getTrack(trackId)?.copy(
            isComplete = true
        ) ?: throw Exception("Track (id ${trackId}) not found")
        downloadDao.upsert(track.toLocal())
    }

    override suspend fun deleteTrack(trackId: String) {
        try {
            downloadDao.deleteById(trackId)
        }catch (e: Exception){
            println(e.message)
        }
    }

    override suspend fun deleteAllTracks() {
        downloadDao.deleteAll()
    }
}