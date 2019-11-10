package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetStationsUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<GetStationBoardResult, String>() {

    override suspend fun run(params: String) = nreRepository.getDeparturesAtStation(params)

}