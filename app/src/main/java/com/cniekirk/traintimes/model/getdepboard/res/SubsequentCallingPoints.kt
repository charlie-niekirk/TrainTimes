package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt7:subsequentCallingPoints")
data class SubsequentCallingPoints(
    @Element val subsequentCallingPoints: List<CallingPointsList>
)