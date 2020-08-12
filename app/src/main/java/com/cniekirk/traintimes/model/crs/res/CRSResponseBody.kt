package com.cniekirk.traintimes.model.crs.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Body")
data class CRSResponseBody(
    @Element val stationListResponse: GetStationListResponse
)
