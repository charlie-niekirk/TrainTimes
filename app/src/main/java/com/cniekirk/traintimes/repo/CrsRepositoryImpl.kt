package com.cniekirk.traintimes.repo

import android.util.Log
import com.cniekirk.traintimes.data.local.AppDb
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

    override fun getCrsCodes(): Either<Failure, List<CRS>> {

        // Access DB first
        crsDao.getCrsCodes().value?.let {
            // DB has data
            return Either.Right(it)
        } ?: run {
            return when (networkHandler.isConnected) {
                true -> request(crsService.getCrsCodes()) { saveCrsCodes(it) }
                false, null -> Either.Left(Failure.NetworkConnectionError())
            }
        }

    }

    private fun saveCrsCodes(responseBody: ResponseBody): List<CRS> {

        val rows: List<Map<String, String>> = csvReader().readAllWithHeader(responseBody.string())
        crsDao.insertAll(rows.map {
            CRS(it.getValue("Station Name"), it.getValue("CRS Code"))
        })

        return crsDao.getCrsCodes().value!!

    }

}