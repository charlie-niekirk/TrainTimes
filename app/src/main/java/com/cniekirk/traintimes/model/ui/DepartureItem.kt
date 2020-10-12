package com.cniekirk.traintimes.model.ui

import com.cniekirk.traintimes.model.getdepboard.res.Service

sealed class DepartureItem {
    object LoadBeforeItem : DepartureItem()
    data class DepartureServiceItem(val service: Service, var isCircular: Boolean = false): DepartureItem()
    object LoadAfterItem : DepartureItem()
}