package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml
import java.util.*

@Xml(name = "GetBoardWithDetailsResult")
data class GetStationBoardResult(
    @Attribute(name = "xmlns:t6") val t6: String,
    @Attribute(name = "xmlns:t9") val t9: String,
    @Attribute(name = "xmlns:c2") val c2: String,
    @Attribute(name = "xmlns:c5") val c5: String,
    @Attribute(name = "xmlns:t12") val t12: String,
    @Attribute(name = "xmlns:t8") val t8: String,
    @Attribute(name = "xmlns:t3") val t3: String,
    @Attribute(name = "xmlns:t11") val t11: String,
    @Attribute(name = "xmlns:c6") val c6: String,
    @Attribute(name = "xmlns:t4") val t4: String,
    @Attribute(name = "xmlns:c3") val c3: String,
    @Attribute(name = "xmlns:t7") val t7: String,
    @Attribute(name = "xmlns:t1") val t1: String,
    @Attribute(name = "xmlns:c4") val c4: String,
    @Attribute(name = "xmlns:t2") val t2: String,
    @Attribute(name = "xmlns:t5") val t5: String,
    @Attribute(name = "xmlns:t") val t: String,
    @Attribute(name = "xmlns:c1") val c1: String,


    @PropertyElement(name = "t6:generatedAt") val generatedAt: String,
    @PropertyElement(name = "t6:locationName") val locationName: String,
    @PropertyElement(name = "t6:crs") val stationCode: String,
    @Element(name = "t6:nrccMessages") val nrccMessages: NrccMessages?,
//    @PropertyElement(name = "lt4:platformAvailable") val platformAvailable: Boolean,
    @Element(name = "t12:trainServices") val trainServices: TrainServices?
)