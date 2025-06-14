package com.holymusic.app.features.domain.use_case

import com.google.gson.Gson
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.entity.SubStatus
import com.holymusic.app.features.data.remote.model.ErrorDto
import com.holymusic.app.features.data.remote.model.SubStatusDto
import com.holymusic.app.features.domain.repository.PaymentApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetSubscriptionsUseCase @Inject constructor(private val paymentApiRepo: PaymentApiRepo) {

    operator fun invoke(body: SubStatus): Flow<Async<SubStatusDto>> =
        flow {
            try {
                emit(Async.Loading())
                val status = paymentApiRepo.getSubStatus(body)
                emit(Async.Success(status))
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