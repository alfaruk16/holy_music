package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.AlbumDto
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetAlbumByTypeUseCase @Inject constructor(private val apiRepo: ApiRepo) {
    operator fun invoke(type: String): kotlinx.coroutines.flow.Flow<Async<AlbumDto>> = flow {
        try {
            emit(Async.Loading())
            val tracks = apiRepo.getAlbumByType(type)
            emit(Async.Success(tracks.copy(data = tracks.data?.filter { it.isActive == true }
                ?.reversed()?.sortedBy { it.sequenceNo })))
        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}