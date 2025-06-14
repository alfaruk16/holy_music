package com.holymusic.app.features.data.repository


import com.holymusic.app.features.data.remote.PaymentApis
import com.holymusic.app.features.data.remote.entity.AmarPay
import com.holymusic.app.features.data.remote.entity.BKash
import com.holymusic.app.features.data.remote.entity.Banglalink
import com.holymusic.app.features.data.remote.entity.BkashCancel
import com.holymusic.app.features.data.remote.entity.BkashToken
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
import com.holymusic.app.features.domain.repository.PaymentApiRepo
import javax.inject.Inject

class PaymentApiRepoImpl @Inject constructor(private val paymentApis: PaymentApis) :
    PaymentApiRepo {
    override suspend fun initiateSSL(body: SSL): SSLResponseDto {
        return paymentApis.initiateSSL(body)
    }

    override suspend fun getSubStatus(body: SubStatus): SubStatusDto {
        return paymentApis.getSubStatus(body = body)
    }

    override suspend fun cancelPlan(body: SSL): CancelDto {
        return paymentApis.cancelPlan(body)
    }

    override suspend fun initiateAmarPay(body: AmarPay): AmarPayResponseDto {
        return paymentApis.initiateAmarPay(body)
    }

    override suspend fun banglalink(body: Banglalink): TelcoResponseDto {
        return paymentApis.initiateBanglalink(body)
    }

    override suspend fun bkashCancelPlan(body: BkashCancel): CancelDto {
        return paymentApis.bkashCancelPlan(body)
    }

    override suspend fun bKashToken(body: BkashToken): BkashTokenResponseDto {
        return paymentApis.getBKashToken(
            username = body.username,
            password = body.password,
            grantType = body.grant_type
        )
    }

    override suspend fun bKash(body: BKash, token: String): BKashResponseDto {
        return paymentApis.initiateBKash(body, token)
    }

    override suspend fun robiPinVerify(body: PinVerify): TelcoResponseDto {
        return paymentApis.robiPinVerify(body)
    }

    override suspend fun banglalinkPinVerify(verify: PinVerify): TelcoResponseDto {
        return paymentApis.banglalinkPinVerify(verify)
    }

    override suspend fun banglalinkCancel(banglalink: Banglalink): TelcoResponseDto {
        return paymentApis.cancelBanglalink(banglalink)
    }

    override suspend fun robi(body: Robi): TelcoResponseDto {
        return paymentApis.initiateRobi(body)
    }
}