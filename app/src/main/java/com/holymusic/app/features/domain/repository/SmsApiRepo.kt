package com.holymusic.app.features.domain.repository

import com.holymusic.app.features.data.remote.entity.CheckOtp
import com.holymusic.app.features.data.remote.entity.Otp
import com.holymusic.app.features.data.remote.entity.SMSRegistration
import com.holymusic.app.features.data.remote.model.OtpDto
import com.holymusic.app.features.data.remote.model.SMSRegistrationResponseDto

interface SmsApiRepo {
    suspend fun requestOtp(otp: Otp): OtpDto

    suspend fun checkOtp(checkOtp: CheckOtp): OtpDto

    suspend fun smsRegistration(body: SMSRegistration): SMSRegistrationResponseDto
}