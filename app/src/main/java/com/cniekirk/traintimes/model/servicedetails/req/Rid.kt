package com.cniekirk.traintimes.model.servicedetails.req

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "ldb:rid")
data class Rid(
    @TextContent val rid: String
)