package com.holymusic.app.features.domain.use_case

import com.holymusic.app.core.util.Async
import com.holymusic.app.features.data.remote.model.TextContentDto
import com.holymusic.app.features.domain.repository.ApiRepo
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class GetTextContentUseCase @Inject constructor(private val apiRepo: ApiRepo) {

    operator fun invoke(id: String): kotlinx.coroutines.flow.Flow<Async<TextContentDto>> = flow {
        try {
            emit(Async.Loading())
            val content = apiRepo.getTextContent(id)
            emit(Async.Success(content))
        } catch (e: HttpException) {
            emit(Async.Error(e.localizedMessage ?: "An unexpected error occurred"))
        } catch (e: IOException) {
            emit(Async.Error("Couldn't reach server. Check your internet connection."))
        }
    }
}