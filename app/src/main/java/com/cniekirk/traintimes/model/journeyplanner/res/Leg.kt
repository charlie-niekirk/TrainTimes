package com.cniekirk.traintimes.model.journeyplanner.res


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import androidx.annotation.Keep

data class Leg(
    @Json(name = "alight")
    val alight: Alight?,
    @Json(name = "board")
    val board: Board?,
    @Json(name = "cateringCodes")
    val cateringCodes: String?,
    @Json(name = "destinationInstants")
    val destinationInstants: DestinationInstants?,
    @Json(name = "destinationPlatform")
    val destinationPlatform: String?,
    @Json(name = "destinations")
    val destinations: String?,
    @Json(name = "id")
    val id: String?,
    @Json(name = "iptisTripIdentifier")
    val iptisTripIdentifier: String?,
    @Json(name = "isReplacementBus")
    val isReplacementBus: String?,
    @Json(name = "mode")
    val mode: String?,
    @Json(name = "operator")
    val `operator`: Operator?,
    @Json(name = "originInstants")
    val originInstants: OriginInstants?,
    @Json(name = "originPlatform")
    val originPlatform: String?,
    @Json(name = "origins")
    val origins: String?,
    @Json(name = "realtimeClassification")
    val realtimeClassification: String?,
    @Json(name = "reservable")
    val reservable: String?,
    @Json(name = "seatingClass")
    val seatingClass: String?,
    @Json(name = "temporaryTrain")
    val temporaryTrain: String?,
    @Json(name = "timetable")
    val timetable: Timetable?,
    @Json(name = "trainCategory")
    val trainCategory: String?,
    @Json(name = "trainRetailID")
    val trainRetailID: String?,
    @Json(name = "trainUID")
    val trainUID: String?,
    @Json(name = "undergroundTravelInformation")
    val undergroundTravelInfo: UndergroundTravelInfo?
)