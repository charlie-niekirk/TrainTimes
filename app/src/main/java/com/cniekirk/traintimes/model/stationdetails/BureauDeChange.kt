package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "BureauDeChange")
data class BureauDeChange(
    @PropertyElement(name = "com:Note") val note: String?,
    @PropertyElement(name = "com:Available") val available: Boolean?
)