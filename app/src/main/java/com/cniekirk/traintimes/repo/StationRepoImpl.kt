package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.data.remote.StationService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.stationdetails.Station
import com.cniekirk.traintimes.utils.NetworkHandler
import com.cniekirk.traintimes.utils.request
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepoImpl @Inject constructor(
    private val networkHandler: NetworkHandler,
    private val stationService: StationService
): StationRepo {

    override fun getStationInfo(crsCode: String): Either<Failure, Station> {

        return when (networkHandler.isConnected) {
            true -> request(stationService.getStationDetails(crsCode)) { it }
            false, null -> Either.Left(Failure.NetworkConnectionError())
        }

    }

}