package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.domain.model.DepartureAtTimeRequest
import com.cniekirk.traintimes.model.getdepboard.res.GetBoardWithDetailsResult
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetDeparturesAtTimeUseCase @Inject constructor(
    private val nreRepository: NreRepository
): BaseUseCase<GetBoardWithDetailsResult, DepartureAtTimeRequest>() {

    override suspend fun run(params: DepartureAtTimeRequest) = nreRepository.getDeparturesAtStation(
        params.from, params.to, params.time
    )

}