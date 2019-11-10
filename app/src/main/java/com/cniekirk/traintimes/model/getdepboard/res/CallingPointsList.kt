package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt7:callingPointList")
data class CallingPointsList(
    @Element val callingPoints: List<CallingPoint>
)