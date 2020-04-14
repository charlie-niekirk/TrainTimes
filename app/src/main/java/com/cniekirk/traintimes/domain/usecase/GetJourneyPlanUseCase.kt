package com.cniekirk.traintimes.domain.usecase

import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRepoRequest
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRequest
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlannerResponse
import com.cniekirk.traintimes.repo.NreRepository
import javax.inject.Inject

class GetJourneyPlanUseCase @Inject constructor(private val nreRepository: NreRepository)
    :BaseUseCase<JourneyPlannerResponse, JourneyPlanRepoRequest>() {

    override suspend fun run(params: JourneyPlanRepoRequest) = nreRepository.getJourneyPlan(params)

}