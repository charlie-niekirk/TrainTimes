package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import java.util.*

@Xml(name = "GetStationBoardResult")
data class GetStationBoardResult(
    @Attribute(name = "xmlns:lt") val lt: String,
    @Attribute(name = "xmlns:lt2") val lt2: String,
    @Attribute(name = "xmlns:lt3") val lt3: String,
    @Attribute(name = "xmlns:lt4") val lt4: String,
    @Attribute(name = "xmlns:lt5") val lt5: String,
    @Attribute(name = "xmlns:lt6") val lt6: String,
    @Attribute(name = "xmlns:lt7") val lt7: String,

    @PropertyElement(name = "lt4:generatedAt") val generatedAt: String,
    @PropertyElement(name = "lt4:locationName") val locationName: String,
    @PropertyElement(name = "lt4:crs") val stationCode: String,
    @Element(name = "lt4:nrccMessages") val nrccMessages: NrccMessages?,
    @PropertyElement(name = "lt4:platformAvailable") val platformAvailable: Boolean,
    @Element(name = "lt7:trainServices") val trainServices: TrainServices
)