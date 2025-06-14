package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.TrackBillboardDto
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetTrackBillboardUseCase @Inject constructor(private val apiRepo: ApiRepo) {

    operator fun invoke(): Flow<Async<TrackBillboardDto>> = flow {
        try {
            emit(Async.Loading())
            val billboard = apiRepo.getTrackBillboard()
            emit(Async.Success(billboard.copy(data = billboard.data?.filter { it.isActive == true }
                ?.reversed()?.sortedBy { it.sequenceNo })))
        } catch (e: HttpException) {
            emit(Async.Error(e.code().toString()))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}