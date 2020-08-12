package com.cniekirk.traintimes.model.crs.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "StationList")
data class StationList(
    @Element val stations: List<CRSStation>
)
