package com.cniekirk.traintimes.model.journeyplanner.req

data class JourneyPlanRepoRequest(
    val origin: String,
    val destination: String,
    val journeyPlanRequest: JourneyPlanRequest
)