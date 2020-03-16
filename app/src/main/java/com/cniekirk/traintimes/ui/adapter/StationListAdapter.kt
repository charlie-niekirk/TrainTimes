package com.cniekirk.traintimes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.data.local.model.CRS
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer

class StationListAdapter(private val stations: List<CRS>,
                         private val onStationItemSelected: OnStationItemSelected
)
    :RecyclerView.Adapter<StationListAdapter.StationListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StationListViewHolder {
        val stationLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.station_list_item, parent, false)
        return StationListViewHolder(
            stationLayout
        )
    }

    override fun onBindViewHolder(holder: StationListViewHolder, position: Int) {
        // TODO: Figure out WTF is going on
        holder.stationName.text = stations[position].crs
        holder.itemView.setOnClickListener { onStationItemSelected.onStationItemClicked(stations[position]) }
    }

    override fun getItemCount() = stations.size

    open class StationListViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        val stationName: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.list_item_station_name)
        }

    }

    interface OnStationItemSelected {
        fun onStationItemClicked(crs: CRS)
    }

}