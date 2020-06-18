package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.stationdetails.Station

interface StationRepo {

    fun getStationInfo(crsCode: String): Either<Failure, Station>

}