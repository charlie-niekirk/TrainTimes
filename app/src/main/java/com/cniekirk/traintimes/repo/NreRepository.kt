package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult

interface NreRepository {

    fun getDeparturesAtStation(station: String, destination: String): Either<Failure, GetStationBoardResult>

}