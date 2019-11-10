package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "lt:services")
data class Message(
    @TextContent val message: String
)