package com.cniekirk.traintimes.model.ui

import com.cniekirk.traintimes.model.getdepboard.res.Service

sealed class DepartureItem {
    object LoadBeforeItem : DepartureItem()
    data class DepartureServiceItem(val service: Service): DepartureItem()
    object LoadAfterItem : DepartureItem()
}