package com.cniekirk.traintimes.model.getdepboard.req

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "v:Header")
data class Header(
    @Element val accessToken: AccessToken
)