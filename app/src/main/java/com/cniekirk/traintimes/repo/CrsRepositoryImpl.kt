package com.cniekirk.traintimes.repo

import android.util.Log
import com.cniekirk.traintimes.data.local.CRSDao
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.data.remote.CRSService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.model.crs.req.CRSRequestBody
import com.cniekirk.traintimes.model.crs.req.CRSRequestEnvelope
import com.cniekirk.traintimes.model.crs.req.GetStationListRequest
import com.cniekirk.traintimes.model.crs.res.CRSResponseEnvelope
import com.cniekirk.traintimes.model.getdepboard.req.AccessToken
import com.cniekirk.traintimes.model.getdepboard.req.Header
import com.cniekirk.traintimes.utils.NetworkHandler
import com.cniekirk.traintimes.utils.extensions.parseEncoded
import com.cniekirk.traintimes.utils.request
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import okhttp3.ResponseBody
import javax.inject.Inject

class CrsRepositoryImpl @Inject constructor(private val networkHandler: NetworkHandler,
                                            private val crsService: CRSService,
                                            private val crsDao: CRSDao): CrsRepository {

    /**
     * Check if CRS codes are cached locally and only request from network if required
     *
     * @param query: optional query [String] to filter results
     * @return [Either] a [Failure] or [List] of [CRS] objects
     */
    override fun getCrsCodes(query: String): Either<Failure, List<CRS>> {

        return if (crsDao.getCrsCodes().isNotEmpty()) {
            val codes = crsDao.getCrsCodes().sortedBy { it.stationName }
            val matches = if (query.isNotEmpty()) {
                codes.filter { location -> location.stationName.contains(query, true)
                    .or(location.crs.contains(query, true)) }
            } else { codes }
            Either.Right(matches)
        } else {

            val req = GetStationListRequest()
            val body = CRSRequestBody(req)
            val env = CRSRequestEnvelope(header = Header(AccessToken()), crsRequestBody = body)

            when (networkHandler.isConnected) {
                true -> request(crsService.getCrsCodes(env)) { saveCrsCodes(it) }
                false, null -> Either.Left(Failure.NetworkConnectionError())
            }
        }

    }

    /**
     * Persist CRS codes mapped to Station names to the DB
     *
     * @param crsResponseEnvelope: XML model object from the webservice response
     * @return [List] of [CRS] objects
     */
    private fun saveCrsCodes(crsResponseEnvelope: CRSResponseEnvelope): List<CRS> {

        crsDao.insertAll(crsResponseEnvelope.crsResponseBody.stationListResponse.getStationListResult.stationList.stations.map {
            CRS(it.stationName.parseEncoded(), it.crs)
        })

        return crsDao.getCrsCodes().sortedBy { it.stationName }

    }

}