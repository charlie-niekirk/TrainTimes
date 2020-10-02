package com.cniekirk.traintimes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.model.getdepboard.local.Query
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer

class FavouritesAdapter(
    private val favourites: List<Query>,
    private val clickListener: FavouritesClickListener
): RecyclerView.Adapter<FavouritesAdapter.FavouritesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouritesViewHolder {
        return FavouritesViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.favourite_item, parent, false))
    }

    override fun onBindViewHolder(holder: FavouritesViewHolder, position: Int) {

        val favourite = favourites[position]
        holder.startStation.text = favourite.fromName

        favourite.toName?.let {
            if (it.isNotEmpty()) {
                holder.endStationTitle.visibility = View.VISIBLE
                holder.endStation.visibility = View.VISIBLE
                holder.endStation.text = favourite.toName
            }
        }

        holder.itemView.setOnClickListener {
            clickListener.onClick(position)
        }

    }

    override fun getItemCount() = favourites.size

    open class FavouritesViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View
            get() = itemView

        val startStation: MaterialTextView by lazy {
            itemView.findViewById(R.id.station_name)
        }
        val endStation: MaterialTextView by lazy {
            itemView.findViewById(R.id.optional_station_name)
        }
        val startStationTitle: MaterialTextView by lazy {
            itemView.findViewById(R.id.from_to_descriptor)
        }
        val endStationTitle: MaterialTextView by lazy {
            itemView.findViewById(R.id.optional_from_to_descriptor)
        }
    }

    interface FavouritesClickListener {
        fun onClick(position: Int)
    }

}