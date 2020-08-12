package com.cniekirk.traintimes.model.crs.req

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Body")
data class CRSRequestBody(
    @Element val stationListRequest: GetStationListRequest
)
