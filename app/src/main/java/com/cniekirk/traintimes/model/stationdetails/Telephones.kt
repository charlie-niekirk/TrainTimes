package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "Telephones")
data class Telephones(
    @PropertyElement(name = "Exists") val exists: Boolean?,
    @PropertyElement(name = "UsageType") val usageType: String?
)