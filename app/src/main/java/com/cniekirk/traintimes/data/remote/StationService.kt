package com.cniekirk.traintimes.data.remote

import com.cniekirk.traintimes.model.stationdetails.Station
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface StationService {
    @GET("/xml/30/station-{crsCode}.xml")
    fun getStationDetails(@Path("crsCode") crsCode: String): Call<Station>
}