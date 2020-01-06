package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "filterType")
data class FilterType(
    @Attribute(name = "i:type") val type: String = "d:string",
    @TextContent val filterType: String
)