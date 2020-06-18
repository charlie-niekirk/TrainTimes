package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "ClosedCircuitTelevision")
data class ClosedCircuitTelevision(
    @PropertyElement(name = "Overall") val hasCctv: Boolean?
)