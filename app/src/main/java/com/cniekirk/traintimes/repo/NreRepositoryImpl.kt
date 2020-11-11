package com.cniekirk.traintimes.repo

import android.util.Log
import com.cniekirk.traintimes.data.local.RecentQueriesDao
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.data.local.model.RecentQuery
import com.cniekirk.traintimes.data.prefs.PreferenceProvider
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.data.remote.TrackTimesService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.delayrepay.DelayRepay
import com.cniekirk.traintimes.model.getdepboard.local.Query
import com.cniekirk.traintimes.model.getdepboard.req.*
import com.cniekirk.traintimes.model.getdepboard.res.GetBoardWithDetailsResult
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRepoRequest
import com.cniekirk.traintimes.model.journeyplanner.res.Journey
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlannerResponse
import com.cniekirk.traintimes.model.servicedetails.req.GetServiceDetailsByRIDRequest
import com.cniekirk.traintimes.model.servicedetails.req.Rid
import com.cniekirk.traintimes.model.servicedetails.req.ServiceDetailsBody
import com.cniekirk.traintimes.model.servicedetails.req.ServiceDetailsEnvelope
import com.cniekirk.traintimes.model.track.req.TrackServiceRequest
import com.cniekirk.traintimes.model.track.res.TrackServiceResponse
import com.cniekirk.traintimes.model.ui.ServiceDetailsUiModel
import com.cniekirk.traintimes.utils.NetworkHandler
import com.cniekirk.traintimes.utils.extensions.hmac
import com.cniekirk.traintimes.utils.extensions.now
import com.cniekirk.traintimes.utils.extensions.parseEncoded
import com.cniekirk.traintimes.utils.request
import com.squareup.moshi.JsonAdapter
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

private const val TAG = "NreRepositoryImpl"

/**
 * Repository implementation for the LDBSVWS web service
 *
 * @author Charles Niekirk
 */
@Singleton
class NreRepositoryImpl @Inject constructor(private val networkHandler: NetworkHandler,
                                            private val nreService: NREService,
                                            private val trackTimesService: TrackTimesService,
                                            private val preferencesProvider: PreferenceProvider,
                                            private val recentQueriesDao: RecentQueriesDao,
                                            private val adapter: JsonAdapter<Query>): NreRepository {

    /**
     * Gets the departing trains along with details about each service from a station
     *
     * @param station: The CRS code of the station to search at
     * @param destination: The (optional) CRS code of the destination station to filter departures by
     *
     * @return An [Either] that exposes a [Failure] or [GetBoardWithDetailsResult]
     */
    override fun getDeparturesAtStation(station: CRS, destination: CRS, time: String): Either<Failure, GetBoardWithDetailsResult> {

        val body = Body(GetDepBoardWithDetailsRequest(
            numRows = "20",
            crs = station.crs,
            filterCrs = destination.crs,
            filterType = "to",
            time = time,
            timeWindow = "120",
            getNonPassengerServices = false))

        val envelope = Envelope(header = Header(AccessToken()), body = body)

        val query = if (destination.stationName.isEmpty()) {
            Query(station.crs, station.stationName)
        } else {
            Query(station.crs, station.stationName, destination.crs, destination.stationName)
        }

        val dbValue = adapter.toJson(query)
        val current = recentQueriesDao.getRecentQueries()
        val isAlready = current.filter { q ->
            q.query.equals(dbValue, true)
        }
        if (isAlready.isNullOrEmpty()) {
            recentQueriesDao.insertRecentQuery(RecentQuery(query = dbValue))
            recentQueriesDao.cleanQueries()
        }

        return when (networkHandler.isConnected) {
            true -> request(nreService.getDepartureBoardWithDetails(envelope)) { env ->
                env.body.getDepBoardWithDetailsResponse.getBoardWithDetailsResult.nrccMessages?.let { nrccMessages ->
                    nrccMessages.messages?.let { messages ->
                        val trimmed = messages.map { message -> message.copy(message = message.message?.parseEncoded()?.parseEncoded()?.replace("\n", "")?.trim()) }
                        val nrcc = nrccMessages.copy(messages = trimmed)
                        return@request env.body.getDepBoardWithDetailsResponse.getBoardWithDetailsResult.copy(nrccMessages = nrcc)
                    }
                } ?: run { return@request env.body.getDepBoardWithDetailsResponse.getBoardWithDetailsResult }
            }
            else -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    /**
     * Gets the arriving trains along with details about each service from a station
     *
     * @param target: The CRS code of the station to search at
     * @param destination: The (optional) CRS code of the source station to filter by
     *
     * @return An [Either] that exposes a [Failure] or [GetBoardWithDetailsResult]
     */
    override fun getArrivalsAtStation(target: String, from: String): Either<Failure, GetBoardWithDetailsResult> {
        val body = ArrBody(GetArrBoardWithDetailsRequest(
            numRows = NumRows(numRows = "20"),
            crs = target,
            filterCrs = from,
            filterType = "to",
            time = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH).now(),
            timeWindow = "120"))
        val envelope = ArrEnvelope(header = Header(AccessToken()), body = body)

        return when (networkHandler.isConnected) {
            true -> request(nreService.getArrivalBoardWithDetails(envelope)) { env ->
                env.body.getArrBoardWithDetailsResponse.getBoardWithDetailsResult.nrccMessages?.let { nrccMessages ->
                    nrccMessages.messages?.let { messages ->
                        val trimmed = messages.map { message -> message.copy(message = message.message?.parseEncoded()?.parseEncoded()?.replace("\n", "")?.trim()) }
                        val nrcc = nrccMessages.copy(messages = trimmed)
                        return@request env.body.getArrBoardWithDetailsResponse.getBoardWithDetailsResult.copy(nrccMessages = nrcc)
                    }
                } ?: run { return@request env.body.getArrBoardWithDetailsResponse.getBoardWithDetailsResult }
            }
            else -> Either.Left(Failure.NetworkConnectionError())
        }
    }

    override fun getRecentQueries(): Either<Failure, List<Query>> {

        return if (recentQueriesDao.getRecentQueries().isNotEmpty()) {

            Either.Right(recentQueriesDao.getRecentQueries().map {
                adapter.fromJson(it.query)!!
            })

        } else {
            Either.Left(Failure.NoRecentQueriesFailure())
        }

    }

    override fun getServiceDetails(rid: String): Either<Failure, ServiceDetailsUiModel> {

        val body = ServiceDetailsBody(GetServiceDetailsByRIDRequest(
            rid = Rid(rid = rid)
        ))
        val envelope = ServiceDetailsEnvelope(header = Header(AccessToken()), serviceDetailsBody = body)

        return when (networkHandler.isConnected) {
            true -> request(nreService.getServiceDetails(envelope)) {
                val serviceDetails = it.body.getServiceDetailsByRIDResponse.getServiceDetailsResult

                Timber.d(serviceDetails.locations.toString())

                var previous = serviceDetails.locations?.locations?.filter { location -> location.departureType.equals("Actual", true) }
                var subsequent = serviceDetails.locations?.locations?.filter { location -> location.departureType.equals("Forecast", true)
                    .or(location.arrivalType.equals("forecast", true))}
                var current = serviceDetails.locations?.locations?.firstOrNull { location ->
                    location.arrivalType.equals("actual", true).and(
                        location.departureType.equals("forecast", true)
                    )
                }

                val validTiplocs = serviceDetails.formation?.formationPoints?.map { point -> point.tiploc.replace(" ", "") }
                validTiplocs?.let {
                    previous = previous?.filter { location -> location.tiploc?.replace(" ", "") in it }
                    subsequent = subsequent?.filter { location -> location.tiploc?.replace(" ", "") in it }
                }

                val uiModel = ServiceDetailsUiModel(serviceDetails.rid, serviceDetails.uid, serviceDetails.trainId, serviceDetails.operator, serviceDetails.operatorCode,
                serviceDetails.serviceType, serviceDetails.category, previous, subsequentLocations = subsequent)

                serviceDetails.locations?.locations?.get(0)?.isCancelled?.let { cancelled ->
                    uiModel.isCancelled = cancelled
                    uiModel.cancelledCallingPoints = serviceDetails.locations.locations
                }

                current?.let { cur -> uiModel.currentLocation = cur }
                uiModel
            }
            else -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    override fun getJourneyPlan(request: JourneyPlanRepoRequest): Either<Failure, JourneyPlannerResponse> {

        // Do this more securely
        val header = "/api/journeyplan/${request.origin}/to/${request.destination}${request.journeyPlanRequest.departTime}"
            .hmac("/api/journeyplan/${request.origin}/to/${request.destination}")

        return when (networkHandler.isConnected) {
            true -> request(trackTimesService.planJourney(request.origin,
                request.destination, request.journeyPlanRequest, header)) {
                val newOutJourneys = ArrayList<Journey>()
                val newReturnJourneys = ArrayList<Journey>()
                // Has a return Leg
                it.inwardJourney?.let { inward ->
                    inward.forEach { inwardJourney ->
                        val returnFares = inwardJourney.fare?.filter { fare ->
                            fare.direction.equals("RETURN", true) }
                        val cheapest = returnFares?.minByOrNull { fare -> fare.totalPrice?.toInt()!! }
                        cheapest?.let { newReturnJourneys.add(inwardJourney.copy(fare = listOf(cheapest))) }
                            ?: run { newReturnJourneys.add(inwardJourney) }
                    }
                }

                it.outwardJourney?.forEach { outward ->
                    val cheapest = outward.fare?.minByOrNull { fare -> fare.totalPrice?.toInt()!! }
                    cheapest?.let { newOutJourneys.add(outward.copy(fare = listOf(cheapest))) }
                        ?: run { newOutJourneys.add(outward) }
                }
                it.copy(outwardJourney = newOutJourneys, inwardJourney = newReturnJourneys)
            }
            else -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    override fun getDelayRepayUrl(operator: String): Either<Failure, DelayRepay> {

        val header = "/api/delayrepay/$operator"
            .hmac("/api/delayrepay/$operator")

        return when (networkHandler.isConnected) {
            true -> request(trackTimesService.getDelayRepayUrl(operator, header)) { it }
            else -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    override fun trackService(trackServiceRequest: TrackServiceRequest): Either<Failure, TrackServiceResponse> {

        val header = "/api/track"
            .hmac("/api/track")

        trackServiceRequest.serviceDetailsUiModel?.let {
            preferencesProvider.saveTrackedServiceDetails(it)
        }

        return when(networkHandler.isConnected) {
            true -> request(trackTimesService.trackService(header, trackServiceRequest)) { it }
            else -> Either.Left(Failure.NetworkConnectionError())
        }

    }

}