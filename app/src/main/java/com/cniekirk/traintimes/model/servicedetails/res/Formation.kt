package com.cniekirk.traintimes.model.servicedetails.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t12:formation")
data class Formation(
    @Element val formationPoints: List<FormationItem>?
)
