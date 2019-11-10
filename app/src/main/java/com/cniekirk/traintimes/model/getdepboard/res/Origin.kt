package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt5:origin")
data class Origin(
    @Element(name = "lt4:location") val location: Location
)