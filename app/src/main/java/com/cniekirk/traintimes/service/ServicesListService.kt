package com.cniekirk.traintimes.service

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.data.local.model.CRS
import com.cniekirk.traintimes.domain.Failure
import com.cniekirk.traintimes.domain.model.State
import com.cniekirk.traintimes.model.getdepboard.res.GetBoardWithDetailsResult
import com.cniekirk.traintimes.model.ui.DepartureItem
import com.cniekirk.traintimes.repo.NreRepository
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val TAG = "ServicesListService"

@AndroidEntryPoint
class ServicesListService : RemoteViewsService() {

    @Inject
    lateinit var nreRepository: NreRepository

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return ListRemoteViewsFactory(this.applicationContext, intent, nreRepository)
    }

}

class ListRemoteViewsFactory(val context: Context, val intent: Intent, val nreRepository: NreRepository):
    RemoteViewsService.RemoteViewsFactory {

    private val widgetItems: MutableList<DepartureItem.DepartureServiceItem> = ArrayList()

    override fun onCreate() {}

    override fun onDataSetChanged() {

        nreRepository.getDeparturesAtStation(
            CRS("London Bridge", "LBG"),
            CRS("", "")
        ).either(::handleFailure, ::handleResponse)

    }

    override fun onDestroy() {
        widgetItems.clear()
    }

    override fun getCount(): Int {
        return widgetItems.size
    }

    override fun getViewAt(position: Int): RemoteViews {


        val platform = if (widgetItems[position].service.platform.isNullOrEmpty()) "TBD" else widgetItems[position].service.platform
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
        val output = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val departureTime = sdf.parse(widgetItems[position].service.scheduledDeparture!!)

        val rv = RemoteViews(context.packageName, R.layout.departure_list_item_widget)
        rv.setTextViewText(R.id.departure_destination_name, widgetItems[position].service.destination.locations[0].locationName)
        rv.setTextViewText(R.id.departure_platform_name, context.getString(R.string.platform_prefix, platform))
        rv.setTextViewText(R.id.scheduled_departure_time, output.format(departureTime!!))

        return rv

    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    private fun handleFailure(failure: Failure) {
        Log.e(TAG, "Failed: $failure")
    }

    private fun handleResponse(response: GetBoardWithDetailsResult) {
        response.trainServices?.let {
            if (widgetItems.isNullOrEmpty()) {
                val allServices = it.trainServices?.map { service -> DepartureItem.DepartureServiceItem(service) }
                allServices?.forEach { depItem ->
                    val subLoc = depItem.service.subsequentLocations?.locations
                    if (depItem.service.origin.location.crs.equals(subLoc!![subLoc.size - 1].stationCode, true)) {
                        depItem.isCircular = true
                    }
                }
                widgetItems.addAll(allServices!!)
            }
        } ?: run {
            widgetItems.clear()
        }
    }

}