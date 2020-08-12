package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t12:origin")
data class Origin(
    @Element(name = "t6:location") val location: Location
)