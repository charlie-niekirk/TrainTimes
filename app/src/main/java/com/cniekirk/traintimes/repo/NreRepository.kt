package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.delayrepay.DelayRepay
import com.cniekirk.traintimes.model.getdepboard.local.Query
import com.cniekirk.traintimes.model.getdepboard.res.GetBoardWithDetailsResult
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRepoRequest
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlannerResponse
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
import com.cniekirk.traintimes.model.track.req.TrackServiceRequest
import com.cniekirk.traintimes.model.track.res.TrackServiceResponse
import com.cniekirk.traintimes.model.ui.ServiceDetailsUiModel
import com.cniekirk.traintimes.utils.extensions.now
import java.text.SimpleDateFormat
import java.util.*

interface NreRepository {

    fun getDeparturesAtStation(station: CRS, destination: CRS, time: String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH).now()): Either<Failure, GetBoardWithDetailsResult>

    fun getRecentQueries(): Either<Failure, List<Query>>

    fun getArrivalsAtStation(target: String, from: String): Either<Failure, GetBoardWithDetailsResult>

    fun getServiceDetails(serviceId: String): Either<Failure, ServiceDetailsUiModel>

    fun getJourneyPlan(request: JourneyPlanRepoRequest): Either<Failure, JourneyPlannerResponse>

    fun getDelayRepayUrl(operator: String): Either<Failure, DelayRepay>

    fun trackService(trackServiceRequest: TrackServiceRequest): Either<Failure, TrackServiceResponse>

}