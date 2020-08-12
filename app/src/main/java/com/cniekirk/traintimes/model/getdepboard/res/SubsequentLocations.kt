package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t12:subsequentLocations")
data class SubsequentLocations(
    @Element val locations: List<Location>
)