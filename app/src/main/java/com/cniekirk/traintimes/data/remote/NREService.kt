package com.cniekirk.traintimes.data.remote

import com.cniekirk.traintimes.model.getdepboard.req.ArrEnvelope
import com.cniekirk.traintimes.model.getdepboard.req.Envelope
import com.cniekirk.traintimes.model.getdepboard.res.GetArrBoardSoapEnvelope
import com.cniekirk.traintimes.model.getdepboard.res.GetDepBoardSoapEnvelope
import com.cniekirk.traintimes.model.servicedetails.ServiceDetails
import com.cniekirk.traintimes.model.servicedetails.req.ServiceDetailsEnvelope
import com.cniekirk.traintimes.model.servicedetails.res.GetServiceDetailsSoapEnvelope
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NREService {
    @POST("/OpenLDBSVWS/ldbsv12.asmx")
    @Headers(
        "Content-Type: text/xml"
    )
    fun getDepartureBoardWithDetails(@Body body: Envelope): Call<GetDepBoardSoapEnvelope>

    @POST("/OpenLDBWS/ldb11.asmx")
    @Headers(
        "Content-Type: text/xml",
        "SOAPAction: http://thalesgroup.com/RTTI/2015-05-14/ldb/GetArrBoardWithDetails"
    )
    fun getArrivalBoardWithDetails(@Body body: ArrEnvelope): Call<GetArrBoardSoapEnvelope>

    @POST("/OpenLDBSVWS/ldbsv12.asmx")
    @Headers(
        "Content-Type: text/xml"
    )
    fun getServiceDetails(@Body body: ServiceDetailsEnvelope): Call<GetServiceDetailsSoapEnvelope>
}