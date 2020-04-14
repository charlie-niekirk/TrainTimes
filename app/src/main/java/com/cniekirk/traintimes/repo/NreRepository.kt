package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.delayrepay.DelayRepay
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRepoRequest
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRequest
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlannerResponse
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
import java.util.*

interface NreRepository {

    fun getDeparturesAtStation(station: String, destination: String): Either<Failure, GetStationBoardResult>

    fun getArrivalsAtStation(destination: String): Either<Failure, GetStationBoardResult>

    fun getServiceDetails(serviceId: String): Either<Failure, GetServiceDetailsResult>

    fun getJourneyPlan(request: JourneyPlanRepoRequest): Either<Failure, JourneyPlannerResponse>

    fun getDelayRepayUrl(operator: String): Either<Failure, DelayRepay>

}