package com.cniekirk.traintimes.model.servicedetails.res

import com.cniekirk.traintimes.model.getdepboard.res.Location
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "t10:locations")
data class Locations(
    @Element val locations: List<Location>?
)