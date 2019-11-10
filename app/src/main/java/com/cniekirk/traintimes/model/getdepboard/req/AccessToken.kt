package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "n0:AccessToken")
data class AccessToken(
    @Attribute(name = "xmlns:n0") val xmlns: String = "http://thalesgroup.com/RTTI/2013-11-28/Token/types",
    @PropertyElement(name = "TokenValue") val tokenValue: String = "d6fe5dae-7c49-425d-b8e8-6d65c74dc972"
)