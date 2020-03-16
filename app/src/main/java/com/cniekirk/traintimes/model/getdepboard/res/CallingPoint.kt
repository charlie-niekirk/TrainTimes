package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt7:callingPoint")
data class CallingPoint(
    @PropertyElement(name = "lt7:locationName") val locationName: String,
    @PropertyElement(name = "lt7:crs") val stationCode: String,
    @PropertyElement(name = "lt7:st") val scheduledTime: String,
    @PropertyElement(name = "lt7:et") val estimatedTime: String?,
    @PropertyElement(name = "lt7:at") val actualTime: String?
)