package com.holymusic.app.features.data.remote

import com.holymusic.app.features.data.remote.entity.Nagad
import com.holymusic.app.features.data.remote.model.NagadResponseDto
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NagadApis {
    @Headers("Content-Type: application/json")
    @POST("api/NgPayInitiate")
    suspend fun intiateNagad(@Body body: Nagad): NagadResponseDto
}