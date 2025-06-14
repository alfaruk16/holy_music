package com.holymusic.app.features.data.repository

import com.holymusic.app.features.data.remote.NagadApis
import com.holymusic.app.features.data.remote.entity.Nagad
import com.holymusic.app.features.data.remote.model.NagadResponseDto
import com.holymusic.app.features.domain.repository.NagadRepo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NagadPayRepoImpl @Inject constructor(private val nagadApis: NagadApis): NagadRepo {
    override suspend fun initiateNagadPay(body: Nagad): NagadResponseDto {
        return nagadApis.intiateNagad(body)
    }
}