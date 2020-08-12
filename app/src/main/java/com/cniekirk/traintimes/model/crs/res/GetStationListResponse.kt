package com.cniekirk.traintimes.model.crs.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GetStationListResponse")
data class GetStationListResponse(
    @Element val getStationListResult: GetStationListResult
)
