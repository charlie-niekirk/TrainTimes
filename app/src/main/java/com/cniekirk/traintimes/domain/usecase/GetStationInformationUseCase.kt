package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.stationdetails.Station
import com.cniekirk.traintimes.repo.StationRepo
import javax.inject.Inject

class GetStationInformationUseCase @Inject constructor(
    private val stationRepo: StationRepo
): BaseUseCase<Station, String>() {
    override suspend fun run(params: String) = stationRepo.getStationInfo(params)
}