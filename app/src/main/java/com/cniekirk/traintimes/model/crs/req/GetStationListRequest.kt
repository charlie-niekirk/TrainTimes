package com.cniekirk.traintimes.model.crs.req

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "ldb:GetStationListRequest")
data class GetStationListRequest(
    @PropertyElement(name = "ldb:currentVersion") val currentVersion: String = "2015-05-14"
)
