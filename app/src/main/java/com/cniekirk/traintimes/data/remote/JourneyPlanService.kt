package com.cniekirk.traintimes.data.remote

import com.cniekirk.traintimes.model.journeyplanner.req.JourneyPlanRequest
import com.cniekirk.traintimes.model.journeyplanner.res.JourneyPlanResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface JourneyPlanService {

    @POST("/api/journeyplan/{fromStation}/to/{toStation}")
    fun planJourney(@Path("fromStation") from: String,
                    @Path("toStation") to: String,
                    @Body journeyPlanRequest: JourneyPlanRequest,
                    @Header("X-Gorgon") authHeader: String)
    : Call<JourneyPlanResponse>

}