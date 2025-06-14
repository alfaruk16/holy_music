package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.flow
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class AddPlayCountScholarUseCase @Inject constructor(private val apiRepo: ApiRepo) {

    operator fun invoke(playCount: RequestBody): kotlinx.coroutines.flow.Flow<Async<ResponseBody>> = flow {
        try {
            emit(Async.Loading())
            val count = apiRepo.addPlayCountScholar(playCount)
            emit(Async.Success(count))
        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}