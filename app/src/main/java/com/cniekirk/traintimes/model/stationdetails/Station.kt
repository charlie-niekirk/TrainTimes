package com.cniekirk.traintimes.model.stationdetails

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "Station")
data class Station(
    @PropertyElement(name = "CrsCode") val crsCode: String?,
    @PropertyElement(name = "Name") val stationName: String?,
    @PropertyElement(name = "Latitude") val latitude: Double?,
    @PropertyElement(name = "Longitude") val longitude: Double?,
    @Element(name = "Address") val address: Address?,
    @Element(name = "Staffing") val staffing: Staffing?,
    @Element(name = "StationFacilities") val stationFacilities: StationFacilities?
)