package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetServiceDetailsUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<GetServiceDetailsResult, String>() {

    override suspend fun run(params: String) = nreRepository.getServiceDetails(params)

}