package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlanResponse
import com.cniekirk.traintimes.model.servicedetails.ServiceDetails
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult

interface NreRepository {

    fun getDeparturesAtStation(station: String, destination: String): Either<Failure, GetStationBoardResult>

    fun getServiceDetails(serviceId: String): Either<Failure, GetServiceDetailsResult>

    fun getJourneyPlan(origin: String, destination: String): Either<Failure, JourneyPlanResponse>

}