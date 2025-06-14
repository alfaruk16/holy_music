package com.holymusic.app.features.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.holymusic.app.features.data.local.entities.LocalDownload
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Query("SELECT * FROM download")
    fun observeAll(): Flow<List<LocalDownload>>

    @Query("SELECT * FROM download WHERE id = :trackId")
    fun observeById(trackId: String): Flow<LocalDownload>

    @Query("SELECT * FROM download")
    suspend fun getAll(): List<LocalDownload>

    @Query("SELECT * FROM download WHERE id = :trackId")
    suspend fun getById(trackId: String): LocalDownload?

    @Upsert
    suspend fun upsert(track: LocalDownload)

    @Upsert
    suspend fun upsertAll(tracks: List<LocalDownload>)

    @Query("DELETE FROM download WHERE id = :trackId")
    suspend fun deleteById(trackId: String): Int

    @Query("DELETE FROM download")
    suspend fun deleteAll()

    @Query("SELECT EXISTS (SELECT * FROM download WHERE id = :trackId)")
    suspend fun isDownloaded(trackId: String): Boolean
}