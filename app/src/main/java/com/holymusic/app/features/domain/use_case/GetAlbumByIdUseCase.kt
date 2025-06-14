package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.SingleAlbumDto
import com.holymusic.app.features.data.remote.model.SingleArtistDto
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetAlbumByIdUseCase @Inject constructor(private val apiRepo: ApiRepo) {

    operator fun invoke(id: String): Flow<Async<SingleAlbumDto>> = flow {
        try {
            emit(Async.Loading())
            val artist = apiRepo.getAlbumById(id = id)
            emit(Async.Success(artist))
        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}