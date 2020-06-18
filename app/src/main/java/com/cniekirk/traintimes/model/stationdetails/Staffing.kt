package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "Staffing")
data class Staffing(
    @PropertyElement(name = "StaffingLevel") val staffingLevel: String?,
    @Element(name = "ClosedCircuitTelevision") val closedCircuitTelevision: ClosedCircuitTelevision?
)