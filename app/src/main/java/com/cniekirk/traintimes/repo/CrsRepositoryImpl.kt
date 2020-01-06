package com.cniekirk.traintimes.repo

import com.cniekirk.traintimes.data.local.CRSDao
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.data.remote.CRSService
import com.cniekirk.traintimes.domain.Either
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.utils.NetworkHandler
import com.cniekirk.traintimes.utils.request
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import okhttp3.ResponseBody
import javax.inject.Inject

class CrsRepositoryImpl @Inject constructor(private val networkHandler: NetworkHandler,
                                            private val crsService: CRSService,
                                            private val crsDao: CRSDao): CrsRepository {

    override fun getCrsCodes(query: String): Either<Failure, List<CRS>> {


        return if (crsDao.getCrsCodes().isNotEmpty()) {
            val codes = crsDao.getCrsCodes()
            val matches = if (query.isNotEmpty()) {
                codes.filter { crs -> crs.crs.contains(query, true)
                    .or(crs.crs.contains(query, true)) }
            } else { codes }
            Either.Right(matches)
        } else {
            when (networkHandler.isConnected) {
                true -> request(crsService.getCrsCodes()) { saveCrsCodes(it) }
                false, null -> Either.Left(Failure.NetworkConnectionError())
            }
        }

    }

    private fun saveCrsCodes(responseBody: ResponseBody): List<CRS> {

        val rows: List<Map<String, String>> = csvReader().readAllWithHeader(responseBody.string())
        crsDao.insertAll(rows.map {
            CRS(it.getValue("CRS Code"), it.getValue("Station Name"))
        })

        return crsDao.getCrsCodes()

    }

}