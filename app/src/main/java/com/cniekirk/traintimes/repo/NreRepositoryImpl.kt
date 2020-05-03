package com.cniekirk.traintimes.repo

import android.util.Log
import com.cniekirk.traintimes.data.remote.TrackTimesService
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.delayrepay.DelayRepay
import com.cniekirk.traintimes.model.getdepboard.req.*
import com.cniekirk.traintimes.model.getdepboard.res.CallingPoint
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRepoRequest
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRequest
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlannerResponse
import com.cniekirk.traintimes.model.journeyplanner.res.OutwardJourney
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
import kotlin.collections.ArrayList

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

    override fun getArrivalsAtStation(destination: String): Either<Failure, GetStationBoardResult> {
        val body = ArrBody(GetArrBoardWithDetailsRequest(
            numRows = NumRows(numRows = "10"),
            crs = Crs(crs = destination),
            filterCrs = FilterCrs(filterCrs = destination),
            filterType = FilterType(filterType = "to"),
            timeOffset = TimeOffset(timeOffset = "0"),
            timeWindow = TimeWindow(timeWindow = "0")))
        val envelope = ArrEnvelope(header = Header(AccessToken()), body = body)

        return when (networkHandler.isConnected) {
            true -> request(nreService.getArrivalBoardWithDetails(envelope)) { it.body.getArrBoardWithDetailsResponse.getStationBoardResult }
            false, null -> Either.Left(Failure.NetworkConnectionError())
        }
    }

    override fun getServiceDetails(serviceId: String): Either<Failure, GetServiceDetailsResult> {

        val body = ServiceDetailsBody(GetServiceDetailsRequest(
            serviceID = ServiceId(serviceID = serviceId)
        ))
        val envelope = ServiceDetailsEnvelope(header = Header(AccessToken()), serviceDetailsBody = body)

        return when (networkHandler.isConnected) {
            true -> request(nreService.getServiceDetails(envelope)) {
                val serviceDetails = it.body.getServiceDetailsResponse.getServiceDetailsResult
                val previous = serviceDetails.previousCallingPoints?.previousCallingPoints?.get(0)?.callingPoints
                val subsequent = serviceDetails.subsequentCallingPoints?.subsequentCallingPoints?.get(0)?.callingPoints
                val realPrev = ArrayList<CallingPoint>()
                val realSub = ArrayList<CallingPoint>()
                previous?.forEach { callingPoint ->
                    callingPoint.actualTime?.let {
                        realPrev.add(callingPoint)
                    } ?: run { realSub.add(callingPoint) }
                }

                val current = CallingPoint(serviceDetails.locationName!!, serviceDetails.stationCode!!,
                    serviceDetails.std!!, serviceDetails.etd, serviceDetails.atd)
                realSub.add(current)

                serviceDetails.previousCallingPoints?.previousCallingPoints?.get(0)?.callingPoints = realPrev
                serviceDetails.subsequentCallingPoints?.subsequentCallingPoints?.get(0)?.callingPoints = (realSub + subsequent!!).toMutableList()
                serviceDetails
            }
            false, null -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    override fun getJourneyPlan(request: JourneyPlanRepoRequest): Either<Failure, JourneyPlannerResponse> {

        // Do this more securely
        val header = "/api/journeyplan/${request.origin}/to/${request.destination}${request.journeyPlanRequest.departTime}"
            .hmac("/api/journeyplan/${request.origin}/to/${request.destination}")

        return when (networkHandler.isConnected) {
            true -> request(trackTimesService.planJourney(request.origin,
                request.destination, request.journeyPlanRequest, header)) {
                val newJourneys = ArrayList<OutwardJourney>()
                it.outwardJourney?.forEach { outward ->
                    val cheapest = outward.fare?.minBy { fare -> fare.totalPrice?.toInt()!! }
                    cheapest?.let { newJourneys.add(outward.copy(fare = listOf(cheapest))) }
                        ?: run { newJourneys.add(outward) }
                }
                it.copy(outwardJourney = newJourneys)
            }
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