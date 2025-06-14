package com.holymusic.app.features.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.holymusic.app.features.data.local.entities.LocalDownload
import com.holymusic.app.features.data.local.entities.LocalTrack


@Database(entities = [LocalDownload::class, LocalTrack::class], version = 3, exportSchema = false)
abstract class HolyTuneDatabase : RoomDatabase() {
    abstract fun downloadDao(): DownloadDao

    abstract fun trackDao(): TrackDao

}