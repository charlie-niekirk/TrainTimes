package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.getdepboard.local.Query
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetRecentQueriesUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<List<Query>, Unit?>() {

    override suspend fun run(params: Unit?) = nreRepository.getRecentQueries()

}