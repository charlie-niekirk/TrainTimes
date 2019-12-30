package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure

interface CrsRepository {

    fun getCrsCodes(): Either<Failure, List<CRS>>

}