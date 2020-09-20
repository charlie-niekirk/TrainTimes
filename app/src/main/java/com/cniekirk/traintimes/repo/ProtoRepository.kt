package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.Favourites
import kotlinx.coroutines.flow.Flow

interface ProtoRepository {

    suspend fun addFavourite(from: CRS, to: CRS): Either<Failure, Boolean>

    suspend fun removeFavourite(index: Int): Either<Failure, Boolean>

    suspend fun getFavourites(): Either<Failure, Flow<Favourites>>

}