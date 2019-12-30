package com.cniekirk.traintimes.data.remote

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Streaming

interface CRSService {

    @GET("/static/documents/content/station_codes.csv")
    @Streaming
    fun getCrsCodes(): Call<ResponseBody>

}