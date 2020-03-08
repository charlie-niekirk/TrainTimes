package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetArrivalsUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<GetStationBoardResult, Array<String>>() {

    override suspend fun run(params: Array<String>) = nreRepository.getArrivalsAtStation(params[0])

}