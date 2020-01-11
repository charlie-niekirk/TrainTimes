package com.cniekirk.traintimes.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer

class DepatureListAdapter(private val services: List<Service>)
    : RecyclerView.Adapter<DepatureListAdapter.DepartureListViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DepatureListAdapter.DepartureListViewHolder {
        val departureLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.departure_list_item, parent, false)
        return DepatureListAdapter.DepartureListViewHolder(departureLayout)
    }

    override fun getItemCount() = services.size

    override fun onBindViewHolder(
        holder: DepatureListAdapter.DepartureListViewHolder,
        position: Int
    ) {
        val platform = if (services[position].platform.isNullOrEmpty()) "TBD" else services[position].platform
        val destinations = services[position].destination.locations

        holder.departureDestinationName.text = destinations[destinations.size - 1].locationName
        holder.platformName.text = holder.containerView?.context?.getString(R.string.platform_prefix, platform)
        holder.scheduledDepartureTime.text = services[position].scheduledDeparture
        holder.estimatedDepartureTime.text = services[position].estimatedDeparture
    }

    open class DepartureListViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        val departureDestinationName: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.departure_destination_name)
        }
        val platformName: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.departure_platform_name)
        }
        val scheduledDepartureTime: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.scheduled_departure_time)
        }
        val estimatedDepartureTime: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.estimated_departure_time)
        }
    }


}