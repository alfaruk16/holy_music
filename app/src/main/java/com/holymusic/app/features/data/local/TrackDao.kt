package com.holymusic.app.features.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.holymusic.app.features.data.local.entities.LocalTrack

@Dao
interface TrackDao {

    @Query("SELECT * FROM track")
    suspend fun getAll(): List<LocalTrack>

    @Query("SELECT * FROM track WHERE contentCategory = :type ORDER BY createdOn DESC LIMIT :limit OFFSET :offset")
    suspend fun getAllByType(type: String, limit: Int = Int.MAX_VALUE, offset: Int = 0): List<LocalTrack>

    @Query("SELECT * FROM track WHERE id = :trackId")
    suspend fun getById(trackId: String): LocalTrack?

    @Upsert
    suspend fun upsert(track: LocalTrack)

    @Upsert
    suspend fun upsertAll(tracks: List<LocalTrack>)

    @Query("DELETE FROM track WHERE id = :trackId")
    suspend fun deleteById(trackId: String): Int

    @Query("DELETE FROM track")
    suspend fun deleteAll()

}