package com.cniekirk.traintimes.repo

import android.util.Log
import com.cniekirk.traintimes.data.remote.NREService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.getdepboard.req.*
import com.cniekirk.traintimes.model.getdepboard.res.GetStationBoardResult
import com.cniekirk.traintimes.utils.NetworkHandler
import com.tickaroo.tikxml.TikXml
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NreRepositoryImpl @Inject constructor(private val networkHandler: NetworkHandler,
                                            private val nreService: NREService,
                                            private val tikXml: TikXml): NreRepository {

    override fun getDeparturesAtStation(station: String): Either<Failure, GetStationBoardResult> {

        val body = Body(GetDepBoardWithDetailsRequest(
            numRows = NumRows(numRows = "10"),
            crs = Crs(crs = station),
            timeOffset = TimeOffset(timeOffset = "0"),
            timeWindow = TimeWindow(timeWindow = "0")))
        val envelope = Envelope(header = Header(AccessToken()), body = body)

        return when (networkHandler.isConnected) {
            true -> request(nreService.getDepartureBoardWithDetails(envelope)) { it.body.getDepBoardWithDetailsResponse.getStationBoardResult }
            false, null -> Either.Left(Failure.NetworkConnectionError())
        }

    }

    /**
     * @param call: The retrofit call
     * @param transform: The transformation function to apply if needed
     * @return An [Either] monad representing a [Failure] or an [R] response
     */
    private fun <T, R> request(call: Call<T>, transform: (T) -> R): Either<Failure, R> {
        return try {
            val response = call.execute()
            Log.d("REPO", "Res: ${response.body().toString()}")
            when (response.isSuccessful) {
                true -> {
                    response.body()?.let {
                        Either.Right(transform(it))
                    } ?: Either.Left(Failure.ServerError())
                }
                false -> {
                    Either.Left(Failure.ServerError())
                }
            }
        } catch (exception: Throwable) {
            Log.e("IMGUR", "Error: $exception")
            Either.Left(Failure.ServerError())
        }
    }

}