package com.holymusic.app.features.data.remote

import com.holymusic.app.features.data.remote.entity.AmarPay
import com.holymusic.app.features.data.remote.entity.BKash
import com.holymusic.app.features.data.remote.entity.Banglalink
import com.holymusic.app.features.data.remote.entity.BkashCancel
import com.holymusic.app.features.data.remote.entity.Robi
import com.holymusic.app.features.data.remote.entity.PinVerify
import com.holymusic.app.features.data.remote.entity.SSL
import com.holymusic.app.features.data.remote.entity.SubStatus
import com.holymusic.app.features.data.remote.model.AmarPayResponseDto
import com.holymusic.app.features.data.remote.model.BKashResponseDto
import com.holymusic.app.features.data.remote.model.BkashTokenResponseDto
import com.holymusic.app.features.data.remote.model.CancelDto
import com.holymusic.app.features.data.remote.model.SSLResponseDto
import com.holymusic.app.features.data.remote.model.SubStatusDto
import com.holymusic.app.features.data.remote.model.TelcoResponseDto
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface PaymentApis {
    @Headers("Content-Type: application/json")
    @POST("api/SSLPayInitiateDP")
    suspend fun initiateSSL(@Body ssl: SSL): SSLResponseDto

    @Headers("Content-Type: application/json")
    @POST("api/subsstatusht")
    suspend fun getSubStatus(@Body body: SubStatus): SubStatusDto

    @Headers("Content-Type: application/json")
    @POST("api/SSLPayCancelSubsDP")
    suspend fun cancelPlan(@Body body: SSL): CancelDto

    @Headers("Content-Type: application/json")
    @POST("api/bkcancelsubs")
    suspend fun bkashCancelPlan(@Body body: BkashCancel): CancelDto

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("token")
    suspend fun getBKashToken(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String
    ): BkashTokenResponseDto

    @Headers("Content-Type: application/json")
    @POST("api/bkpayinitiate")
    suspend fun initiateBKash(
        @Body ssl: BKash,
        @Header("Authorization") token: String
    ): BKashResponseDto

    @Headers("Content-Type: application/json")
    @POST("api/amrpayinitiate")
    suspend fun initiateAmarPay(@Body ssl: AmarPay): AmarPayResponseDto

    @Headers("Content-Type: application/json")
    @POST("api/BLSendSubsUnSubsOTP")
    suspend fun initiateBanglalink(@Body ssl: Banglalink): TelcoResponseDto

    @Headers("Content-Type: application/json")
    @POST("api/RobiSendSubsUnSubs")
    suspend fun initiateRobi(@Body ssl: Robi): TelcoResponseDto

    @Headers("Content-Type: application/json")
    @POST("api/RobiSendSubsUnSubsVerify")
    suspend fun robiPinVerify(@Body ssl: PinVerify): TelcoResponseDto

    @Headers("Content-Type: application/json")
    @POST("api/BLSendSubsUnSubsVerify")
    suspend fun banglalinkPinVerify(@Body ssl: PinVerify): TelcoResponseDto

    @Headers("Content-Type: application/json")
    @POST("api/BLSendSubsUnSubs")
    suspend fun cancelBanglalink(@Body ssl: Banglalink): TelcoResponseDto
}
