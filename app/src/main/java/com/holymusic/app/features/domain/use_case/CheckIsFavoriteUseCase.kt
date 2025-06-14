package com.holymusic.app.features.domain.use_case

import com.google.gson.Gson
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.DefaultDto
import com.holymusic.app.features.data.remote.model.ErrorDto
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CheckIsFavoriteUseCase @Inject constructor(private val apiRepo: ApiRepo) {

    operator fun invoke(trackId: String): Flow<Async<DefaultDto>> = flow {
        try {
            emit(Async.Loading())
            val isFavorite = apiRepo.isFavorite(trackId)
            emit(Async.Success(isFavorite))
        } catch (e: HttpException) {
            val error: ErrorDto = Gson().fromJson(
                e.response()?.errorBody()?.charStream(),
                ErrorDto::class.java
            )
            emit(Async.Error(error.message ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }

}