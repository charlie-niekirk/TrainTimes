package com.cniekirk.traintimes.model.ui

import com.cniekirk.traintimes.model.getdepboard.res.Location

data class ServiceDetailsUiModel(
    val rid: String?,
    val uid: String?,
    val trainId: String?,
    val operator: String?,
    val operatorCode: String?,
    val serviceType: String?,
    val category: String?,
    val previousLocations: List<Location>?,
    var currentLocation: Location? = null,
    val subsequentLocations: List<Location>?
)