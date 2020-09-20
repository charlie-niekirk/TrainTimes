package com.cniekirk.traintimes.repo

import androidx.datastore.DataStore
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.Favourite
import com.cniekirk.traintimes.model.Favourites
import javax.inject.Inject

class ProtoRepositoryImpl @Inject constructor(
    private val favouritesDataStore: DataStore<Favourites>
): ProtoRepository {

    override suspend fun addFavourite(from: CRS, to: CRS): Either<Failure, Boolean> {

        val favourite = if (to.stationName.isEmpty()) {
            Favourite.newBuilder()
                .setFromCrs(from.crs)
                .setFromStationName(from.stationName)
                .build()
        } else {
            Favourite.newBuilder()
                .setFromCrs(from.crs)
                .setFromStationName(from.stationName)
                .setToCrs(to.crs)
                .setToStationName(to.stationName)
                .build()
        }

        favouritesDataStore.updateData { favourites ->
            favourites.toBuilder().addFavourite(favourite).build()
        }

        return Either.Right(true)
    }

    override suspend fun removeFavourite(index: Int): Either<Failure, Boolean> {

        favouritesDataStore.updateData { favourites ->
            favourites.toBuilder().removeFavourite(index).build()
        }

        return Either.Right(true)

    }

    override suspend fun getFavourites() = Either.Right(favouritesDataStore.data)

}