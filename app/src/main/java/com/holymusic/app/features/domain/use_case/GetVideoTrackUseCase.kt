package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetVideoTrackUseCase @Inject constructor(private val apiRepo: ApiRepo) {

    operator fun invoke(id: String): Flow<Async<TracksDto>> = flow {
        try {
            emit(Async.Loading())
            val tracks = apiRepo.getVideoTracks(id)
            emit(Async.Success(tracks.copy(data = tracks.data?.filter { it.isActive ?: false })))
        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}