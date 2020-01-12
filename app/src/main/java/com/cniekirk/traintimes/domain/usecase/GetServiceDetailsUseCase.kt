package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.servicedetails.ServiceDetails
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetServiceDetailsUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<ServiceDetails, String>() {

    override suspend fun run(params: String) = nreRepository.getServiceDetails(params)

}