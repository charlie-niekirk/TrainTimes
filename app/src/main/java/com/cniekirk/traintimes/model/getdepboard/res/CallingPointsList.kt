package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t10:location")
data class CallingPointsList(
    @Element var callingPoints: MutableList<CallingPoint>
)