package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetTracksByArtistUseCase @Inject constructor(private val apiRepo: ApiRepo) {

    operator fun invoke(artistId: String, type: String): Flow<Async<TracksDto>> = flow {
        try {
            emit(Async.Loading())
            val tracks = apiRepo.getTracksByArtist(artistId, type)
            emit(Async.Success(tracks.copy(data = tracks.data?.filter { it.isActive == true }
                ?.sortedBy { it.sequenceNo })))
        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (_: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}