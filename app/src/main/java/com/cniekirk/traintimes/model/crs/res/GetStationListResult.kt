package com.cniekirk.traintimes.model.crs.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "GetStationListResult")
data class GetStationListResult(
    @Element val stationList: StationList
)
