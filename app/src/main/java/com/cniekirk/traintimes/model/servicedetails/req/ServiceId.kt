package com.cniekirk.traintimes.model.servicedetails.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "serviceID")
data class ServiceId(
    @Attribute(name = "i:type") val type: String = "d:string",
    @TextContent val serviceID: String
)