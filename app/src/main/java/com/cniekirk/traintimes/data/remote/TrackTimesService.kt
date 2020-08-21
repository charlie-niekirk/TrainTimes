package com.cniekirk.traintimes.data.remote

import com.cniekirk.traintimes.model.delayrepay.DelayRepay
import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRequest
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlannerResponse
import com.cniekirk.traintimes.model.track.req.TrackServiceRequest
import com.cniekirk.traintimes.model.track.res.TrackServiceResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TrackTimesService {

    @POST("/api/journeyplan/{fromStation}/to/{toStation}")
    fun planJourney(@Path("fromStation") from: String,
                    @Path("toStation") to: String,
                    @Body journeyPlanRequest: JourneyPlanRequest,
                    @Header("X-Gorgon") authHeader: String)
    : Call<JourneyPlannerResponse>

    @GET("/api/delayrepay/{operator}")
    fun getDelayRepayUrl(@Path("operator") operator: String,
                         @Header("X-Gorgon") authHeader: String)
    : Call<DelayRepay>

    @POST("/api/track")
    fun trackService(@Header("X-Gorgon") authHeader: String,
                     @Body trackServiceRequest: TrackServiceRequest)
    : Call<TrackServiceResponse>

}