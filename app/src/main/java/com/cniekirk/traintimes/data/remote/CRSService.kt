package com.cniekirk.traintimes.data.remote

import com.cniekirk.traintimes.model.crs.req.CRSRequestEnvelope
import com.cniekirk.traintimes.model.crs.res.CRSResponseEnvelope
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface CRSService {

    @POST("/OpenLDBSVWS/ldbsvref.asmx")
    @Headers(
        "Content-Type: text/xml"
    )
    fun getCrsCodes(@Body body: CRSRequestEnvelope): Call<CRSResponseEnvelope>

}