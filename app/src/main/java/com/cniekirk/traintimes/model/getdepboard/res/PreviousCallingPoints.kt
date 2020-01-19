package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt7:previousCallingPoints")
data class PreviousCallingPoints(
    @Element val previousCallingPoints: List<CallingPointsList>
)