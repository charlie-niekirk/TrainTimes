package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlanResponse
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetJourneyPlanUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<JourneyPlanResponse, Array<String>>() {

    override suspend fun run(params: Array<String>) = nreRepository.getJourneyPlan(params[0], params[1])

}