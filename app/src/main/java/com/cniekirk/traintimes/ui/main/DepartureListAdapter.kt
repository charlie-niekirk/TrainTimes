package com.cniekirk.traintimes.ui.main

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer

class DepartureListAdapter(private val services: List<Service>,
                           private val clickListener: DepartureItemClickListener)
    : RecyclerView.Adapter<DepartureListAdapter.DepartureListViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val item = services[position]
        return item.hashCode().toLong()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DepartureListViewHolder {
        val departureLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.departure_list_item, parent, false)
        return DepartureListViewHolder(departureLayout)
    }

    override fun getItemCount() = services.size

    override fun onBindViewHolder(
        holder: DepartureListViewHolder,
        position: Int
    ) {

        val platform = if (services[position].platform.isNullOrEmpty()) "TBD" else services[position].platform
        val destinations = services[position].destination.locations

        holder.itemView.transitionName = "${holder.itemView.context.getString(R.string.departure_background_transition)}-$position"

        holder.departureDestinationName.text = destinations[destinations.size - 1].locationName
        holder.departureDestinationName.transitionName = "${holder.itemView.context.getString(R.string.departure_text_transition)}-$position"

        holder.platformName.text = holder.containerView?.context?.getString(R.string.platform_prefix, platform)
        holder.scheduledDepartureTime.text = services[position].scheduledDeparture
        holder.tocName.text = services[position].operator
        setPillColor(holder.tocName.text.toString(), holder)

        val etd = services[position].estimatedDeparture
        if (!etd.equals("On Time", ignoreCase = true)) {
            holder.scheduledDepartureTime.paintFlags =
                (holder.scheduledDepartureTime.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG))
            holder.estimatedDepartureTime
                .setTextColor(holder.itemView.resources.getColor(R.color.colorRed, null))
        } else {
            holder.estimatedDepartureTime
                .setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
        }

        holder.estimatedDepartureTime.text = services[position].estimatedDeparture
        holder.itemView.setOnClickListener { clickListener.onClick(position, holder.itemView, holder.departureDestinationName) }
    }

    private fun setPillColor(toc: String, holder: DepartureListViewHolder) {

        holder.tocName.setTextColor(holder.tocName.resources.getColor(android.R.color.white, null))

        when (toc.toLowerCase()) {
            "tfl rail" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTflRail, null))
            }
            "great western railway" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGwr, null))
            }
            "northern" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocNorthern, null))
            }
            else -> holder.tocName.apply {
                holder.tocName.setTextColor(holder.tocName.resources.getColor(android.R.color.black, null))
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.colorAccent, null))

            }
        }
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
        val tocName: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.toc_name)
        }
        val scheduledDepartureTime: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.scheduled_departure_time)
        }
        val estimatedDepartureTime: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.estimated_departure_time)
        }
    }

    interface DepartureItemClickListener {
        fun onClick(position: Int, itemBackground: View, destinationText: MaterialTextView)
    }


}