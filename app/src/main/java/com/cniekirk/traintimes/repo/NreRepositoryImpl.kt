package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.data.remote.TrackTimesService
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.delayrepay.DelayRepay
import com.cniekirk.traintimes.model.getdepboard.req.*
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRequest
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlanResponse
import com.cniekirk.traintimes.model.servicedetails.req.GetServiceDetailsRequest
import com.cniekirk.traintimes.model.servicedetails.req.ServiceDetailsBody
import com.cniekirk.traintimes.model.servicedetails.req.ServiceDetailsEnvelope
import com.cniekirk.traintimes.model.servicedetails.req.ServiceId
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsResult
import com.cniekirk.traintimes.utils.NetworkHandler
import com.cniekirk.traintimes.utils.extensions.hmac
import com.cniekirk.traintimes.utils.request
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NreRepositoryImpl @Inject constructor(private val networkHandler: NetworkHandler,
                                            private val nreService: NREService,
                                            private val trackTimesService: TrackTimesService): NreRepository {

    override fun getDeparturesAtStation(station: String, destination: String): Either<Failure, GetStationBoardResult> {

        val body = Body(GetDepBoardWithDetailsRequest(
            numRows = NumRows(numRows = "10"),
            crs = Crs(crs = station),
            filterCrs = FilterCrs(filterCrs = destination),
            filterType = FilterType(filterType = "to"),
            timeOffset = TimeOffset(timeOffset = "0"),
            timeWindow = TimeWindow(timeWindow = "0")))
        val envelope = Envelope(header = Header(AccessToken()), body = body)

        return when (networkHandler.isConnected) {
            true -> request(nreService.getDepartureBoardWithDetails(envelope)) { it.body.getDepBoardWithDetailsResponse.getStationBoardResult }
            false, null -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    override fun getServiceDetails(serviceId: String): Either<Failure, GetServiceDetailsResult> {

        val body = ServiceDetailsBody(GetServiceDetailsRequest(
            serviceID = ServiceId(serviceID = serviceId)
        ))
        val envelope = ServiceDetailsEnvelope(header = Header(AccessToken()), serviceDetailsBody = body)

        return when (networkHandler.isConnected) {
            true -> request(nreService.getServiceDetails(envelope)) { it.body.getServiceDetailsResponse.getServiceDetailsResult }
            false, null -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    override fun getJourneyPlan(origin: String, destination: String): Either<Failure, JourneyPlanResponse> {

        val time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH).format(Date())

        // Do this more securely
        val header = "/api/journeyplan/$origin/to/$destination$time"
            .hmac("/api/journeyplan/$origin/to/$destination")

        return when (networkHandler.isConnected) {
            true -> request(trackTimesService.planJourney(origin,
                destination, JourneyPlanRequest(time), header)) { it }
            false, null -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    override fun getDelayRepayUrl(operator: String): Either<Failure, DelayRepay> {

        val header = "/api/delayrepay/$operator"
            .hmac("/api/delayrepay/$operator")

        return when (networkHandler.isConnected) {
            true -> request(trackTimesService.getDelayRepayUrl(operator, header)) { it }
            false, null -> Either.Left(Failure.NetworkConnectionError())
        }

    }

}