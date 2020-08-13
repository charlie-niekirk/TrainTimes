package com.cniekirk.traintimes.model.servicedetails.res

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t12:fmloc")
data class FormationItem(
    @Attribute(name = "tiploc") val tiploc: String
)
