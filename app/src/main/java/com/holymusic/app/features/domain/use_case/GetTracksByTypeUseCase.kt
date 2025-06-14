package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.TracksDto
import com.holymusic.app.features.domain.repository.ApiRepo
import com.holymusic.app.features.domain.repository.TrackRepo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetTracksByTypeUseCase @Inject constructor(
    private val apiRepo: ApiRepo,
    private val trackRepo: TrackRepo
) {

    operator fun invoke(
        type: String,
        page: Int = 1,
        take: Int = 0
    ): Flow<Async<TracksDto>> = flow {

        try {
            emit(Async.Loading())

            val data = trackRepo.getTrackByType(
                type,
                limit = if (take == 0) Int.MAX_VALUE else take,
                offset = (page - 1) * take
            )
            if (data.isNotEmpty()) {
                emit(
                    Async.Success(
                        TracksDto(
                            data = data,
                            totalRecords = data.size,
                            totalPage = data.size / if (take == 0) 50 else take
                        )
                    )
                )
            }
            val tracks =
                apiRepo.getTracksByType(type = type, skip = page.toString(), take = take.toString())
            emit(Async.Success(tracks.copy(data = tracks.data?.filter { it.isActive == true }
                ?.sortedBy { it.sequenceNo })))

            trackRepo.createTracks(
                tracks.data?.filter { it.isActive == true } ?: emptyList()
            )

        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}