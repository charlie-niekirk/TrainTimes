package com.cniekirk.traintimes.data.remote

import com.cniekirk.traintimes.model.getdepboard.req.Envelope
import com.cniekirk.traintimes.model.getdepboard.res.GetDepBoardSoapEnvelope
import com.cniekirk.traintimes.model.servicedetails.ServiceDetails
import com.cniekirk.traintimes.model.servicedetails.req.ServiceDetailsEnvelope
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsSoapEnvelope
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NREService {
    @POST("/OpenLDBWS/ldb11.asmx")
    @Headers(
        "Content-Type: text/xml",
        "SOAPAction: http://thalesgroup.com/RTTI/2015-05-14/ldb/GetDepBoardWithDetails"
    )
    fun getDepartureBoardWithDetails(@Body body: Envelope): Call<GetDepBoardSoapEnvelope>
    @POST("/OpenLDBWS/ldb11.asmx")
    @Headers(
        "Content-Type: text/xml",
        "SOAPAction: http://thalesgroup.com/RTTI/2012-01-13/ldb/GetServiceDetails"
    )
    fun getServiceDetails(@Body body: ServiceDetailsEnvelope): Call<GetServiceDetailsSoapEnvelope>
}