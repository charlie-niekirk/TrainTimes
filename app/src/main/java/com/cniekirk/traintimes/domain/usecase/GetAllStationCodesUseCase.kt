package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.repo.CrsRepository
import javax.inject.Inject

class GetAllStationCodesUseCase @Inject constructor(private val crsRepository: CrsRepository)
    :BaseUseCase<List<CRS>, Unit?>() {

    override suspend fun run(params: Unit?) = crsRepository.getCrsCodes()

}