package com.cniekirk.traintimes.model.ui

import com.cniekirk.traintimes.R

data class PopupAction(
    val actionName: String,
    val actionIcon: Int
)

object Action {
    private val favourite = PopupAction("Add to favourites", R.drawable.ic_star)
    private val track = PopupAction("Track service", R.drawable.ic_baseline_add_alert)
    private val report = PopupAction("Report disruption", R.drawable.ic_warning)
    val actions = listOf(favourite, track, report)
}