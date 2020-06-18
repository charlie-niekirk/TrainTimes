package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "SeatedArea")
data class SeatedArea(
    @PropertyElement(name = "com:Available") val available: Boolean?
)