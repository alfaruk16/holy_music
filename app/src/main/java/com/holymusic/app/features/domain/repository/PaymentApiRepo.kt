package com.holymusic.app.features.domain.repository

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

interface PaymentApiRepo {
    suspend fun initiateSSL(body: SSL): SSLResponseDto
    suspend fun getSubStatus(body: SubStatus): SubStatusDto
    suspend fun cancelPlan(body: SSL): CancelDto
    suspend fun initiateAmarPay(body: AmarPay): AmarPayResponseDto
    suspend fun banglalink(body: Banglalink): TelcoResponseDto
    suspend fun bkashCancelPlan(body: BkashCancel): CancelDto
    suspend fun bKashToken(body: BkashToken): BkashTokenResponseDto
    suspend fun bKash(body: BKash, token: String): BKashResponseDto
    suspend fun robi(body: Robi): TelcoResponseDto
    suspend fun robiPinVerify(body: PinVerify): TelcoResponseDto
    suspend fun banglalinkPinVerify(verify: PinVerify): TelcoResponseDto
    suspend fun banglalinkCancel(banglalink: Banglalink): TelcoResponseDto
}