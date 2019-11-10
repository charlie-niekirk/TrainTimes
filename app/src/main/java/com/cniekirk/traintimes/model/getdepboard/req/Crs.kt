package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class Crs(
    @Attribute(name = "i:type") val type: String = "d:string",
    @TextContent val crs: String
)