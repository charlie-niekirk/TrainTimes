package com.cniekirk.traintimes.model.getdepboard.res

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "soap:Envelope")
data class GetArrBoardSoapEnvelope(
    @Element(name = "soap:Body") val body: GetArrWithDetailsSoapBody
)