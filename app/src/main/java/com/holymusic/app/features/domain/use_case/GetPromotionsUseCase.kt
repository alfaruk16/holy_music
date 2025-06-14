package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.ProfileDto
import com.holymusic.app.features.data.remote.model.PromotionsDto
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetPromotionsUseCase @Inject constructor(private val apiRepo: ApiRepo) {

    operator fun invoke(): Flow<Async<PromotionsDto>> = flow {
        try {
            emit(Async.Loading())
            val profile = apiRepo.getPromotions()
            emit(Async.Success(profile))
        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}