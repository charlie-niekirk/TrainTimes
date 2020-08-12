package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt:services")
data class Message(
    @PropertyElement(name = "t5:category") val category: String?,
    @PropertyElement(name = "t5:severity") val severity: String?,
    @PropertyElement(name = "t5:xhtmlMessage") val message: String?
)