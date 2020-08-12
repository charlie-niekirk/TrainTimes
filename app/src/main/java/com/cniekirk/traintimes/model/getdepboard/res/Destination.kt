package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t12:destination")
data class Destination(
    @Element val locations: List<StartEndLocation>
)