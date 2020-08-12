package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.delayrepay.DelayRepay
import com.cniekirk.traintimes.model.getdepboard.res.GetBoardWithDetailsResult
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRepoRequest
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlannerResponse
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
import com.cniekirk.traintimes.model.ui.ServiceDetailsUiModel

interface NreRepository {

    fun getDeparturesAtStation(station: String, destination: String): Either<Failure, GetBoardWithDetailsResult>

    fun getArrivalsAtStation(target: String, destination: String): Either<Failure, GetBoardWithDetailsResult>

    fun getServiceDetails(serviceId: String): Either<Failure, ServiceDetailsUiModel>

    fun getJourneyPlan(request: JourneyPlanRepoRequest): Either<Failure, JourneyPlannerResponse>

    fun getDelayRepayUrl(operator: String): Either<Failure, DelayRepay>

}