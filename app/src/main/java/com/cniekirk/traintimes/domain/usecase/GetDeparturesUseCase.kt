package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.model.getdepboard.res.GetBoardWithDetailsResult
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetDeparturesUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<GetBoardWithDetailsResult, Array<CRS>>() {

    override suspend fun run(params: Array<CRS>) = nreRepository.getDeparturesAtStation(params[0], params[1])

}