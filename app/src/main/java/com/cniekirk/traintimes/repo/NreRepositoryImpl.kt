package com.cniekirk.traintimes.repo

import android.util.Log
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.getdepboard.req.*
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.model.servicedetails.ServiceDetails
import com.cniekirk.traintimes.utils.NetworkHandler
import com.cniekirk.traintimes.utils.request
import com.tickaroo.tikxml.TikXml
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NreRepositoryImpl @Inject constructor(private val networkHandler: NetworkHandler,
                                            private val nreService: NREService): NreRepository {

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

    // TODO: Full implementation
    override fun getServiceDetails(serviceId: String): Either<Failure, ServiceDetails> {
        return Either.Right(ServiceDetails(""))
    }

}