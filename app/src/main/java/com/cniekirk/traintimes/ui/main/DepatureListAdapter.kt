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
        val destinations = services[position].destination.locations
        holder.departureDestinationName.text = destinations[destinations.size - 1].locationName
        holder.departureTime.text = services[position].scheduledDeparture
    }

    open class DepartureListViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        val departureDestinationName: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.departure_destination_name)
        }
        val departureTime: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.departure_time)
        }

    }


}