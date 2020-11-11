package com.cniekirk.traintimes.repo

import androidx.datastore.core.DataStore
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.Favourite
import com.cniekirk.traintimes.model.Favourites
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "ProtoRepositoryImpl"

class ProtoRepositoryImpl @Inject constructor(
    private val favouritesDataStore: DataStore<Favourites>
): ProtoRepository {

    override suspend fun addFavourite(from: CRS, to: CRS): Either<Failure, Boolean> {

        val favourite = if (to.stationName.isEmpty()) {
            Favourite(fromCrs = from.crs, fromStationName = from.stationName)
        } else {
            Favourite(fromCrs = from.crs, fromStationName = from.stationName, toCrs = to.crs, toStationName = to.stationName)
        }

        favouritesDataStore.updateData { favourites ->
            Timber.i("Added favourite!")
            favourites.copy(favourites.favourite.plus(favourite))
        }

        return Either.Right(true)
    }

    override suspend fun removeFavourite(index: Int): Either<Failure, Boolean> {

        favouritesDataStore.updateData { favourites ->
            val updated = favourites.favourite.toMutableList().apply { removeAt(index) }
            Favourites(favourite = updated)
        }

        return Either.Right(true)

    }

    override suspend fun getFavourites() = Either.Right(favouritesDataStore.data)

}