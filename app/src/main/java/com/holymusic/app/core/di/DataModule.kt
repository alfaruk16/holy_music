package com.holymusic.app.core.di

import android.content.Context
import androidx.room.Room
import com.holymusic.app.features.data.local.DownloadDao
import com.holymusic.app.features.data.local.HolyTuneDatabase
import com.holymusic.app.features.data.local.TrackDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


//@Module
//@InstallIn(SingletonComponent::class)
//abstract class RepositoryModule{
//
//    @Singleton
//    @Binds
//    abstract fun bindTrackRepo(repo: TrackRepoImpl): TrackRepo
//}

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): HolyTuneDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            HolyTuneDatabase::class.java,
            "HolyTune.db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideDownloadDao(database: HolyTuneDatabase): DownloadDao = database.downloadDao()

    @Provides
    fun provideTrackDao(database: HolyTuneDatabase): TrackDao = database.trackDao()
}