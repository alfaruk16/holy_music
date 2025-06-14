package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.entity.SSL
import com.holymusic.app.features.data.remote.model.CancelDto
import com.holymusic.app.features.domain.repository.PaymentApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class CancelPlanUseCase @Inject constructor(private val paymentApiRepo: PaymentApiRepo) {

    operator fun invoke(body: SSL): Flow<Async<CancelDto>> = flow {
        try {
            emit(Async.Loading())
            val ssl = paymentApiRepo.cancelPlan(body)
            emit(Async.Success(ssl))
        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}