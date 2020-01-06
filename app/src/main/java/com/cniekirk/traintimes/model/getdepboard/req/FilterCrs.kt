package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "filterCrs")
data class FilterCrs(
    @Attribute(name = "i:type") val type: String = "d:string",
    @TextContent val filterCrs: String
)