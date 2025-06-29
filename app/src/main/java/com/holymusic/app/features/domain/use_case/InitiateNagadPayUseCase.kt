package com.holymusic.app.features.domain.use_case

import com.google.gson.Gson
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.entity.Nagad
import com.holymusic.app.features.data.remote.model.ErrorDto
import com.holymusic.app.features.data.remote.model.NagadResponseDto
import com.holymusic.app.features.domain.repository.NagadRepo
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class InitiateNagadPayUseCase @Inject constructor(private val nagadRepo: NagadRepo) {

    operator fun invoke(body: Nagad): kotlinx.coroutines.flow.Flow<Async<NagadResponseDto>> = flow {
        try {
            emit(Async.Loading())
            val nagad = nagadRepo.initiateNagadPay(body)
            emit(Async.Success(nagad))
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