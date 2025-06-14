package com.holymusic.app.core.exoplayer.data.remote

import com.holymusic.app.core.exoplayer.data.entities.Song
import javax.inject.Inject

class MusicDatabase @Inject constructor(
){

    suspend fun getAllSongs(): List<Song> {

        return try {
            listOf(
                Song(
                    mediaId = "46698080",
                    title = "Kolijay Muhammad",
                    subtitle = "Muhammad Badruzzaman"
                ),
                Song(
                    mediaId = "51386569",
                    title = "Koro Rin Porishodh",
                    subtitle = "Muhammad Badruzzaman"
                )
            )
        } catch (e: Exception) {
            emptyList()
        }
    }
}