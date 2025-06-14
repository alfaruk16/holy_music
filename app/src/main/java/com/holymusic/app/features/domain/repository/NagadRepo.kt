package com.holymusic.app.features.domain.repository

import com.holymusic.app.features.data.remote.entity.Nagad
import com.holymusic.app.features.data.remote.model.NagadResponseDto

interface NagadRepo {
    suspend fun initiateNagadPay(body: Nagad): NagadResponseDto
}