package com.holymusic.app.features.domain.use_case

import com.google.gson.Gson
import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.entity.Banglalink
import com.holymusic.app.features.data.remote.model.ErrorDto
import com.holymusic.app.features.data.remote.model.TelcoResponseDto
import com.holymusic.app.features.domain.repository.PaymentApiRepo
import jakarta.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import kotlin.jvm.java

class CancelBanglalinkUseCase @Inject constructor(private val paymentApiRepo: PaymentApiRepo) {
    operator fun invoke(body: Banglalink): Flow<Async<TelcoResponseDto>> = flow {
        try {
            emit(Async.Loading())
            val ssl = paymentApiRepo.banglalinkCancel(body)
            emit(Async.Success(ssl))
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