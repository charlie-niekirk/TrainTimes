package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.repo.CrsRepository
import javax.inject.Inject

class GetStationsUseCase @Inject constructor(private val crsRepository: CrsRepository)
    :BaseUseCase<List<CRS>, String>() {

    override suspend fun run(params: String) = crsRepository.getCrsCodes(params)

}