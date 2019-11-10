package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "numRows")
data class NumRows(
    @Attribute(name = "i:type") val type: String = "d:int",
    @TextContent val numRows: String
)