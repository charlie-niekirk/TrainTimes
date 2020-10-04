package com.cniekirk.traintimes.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.cniekirk.traintimes.utils.extensions.parseEncoded
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer
import java.text.SimpleDateFormat
import java.util.*

class DepartureListAdapter(private val services: List<Service>,
                           private val clickListener: DepartureItemClickListener
) : RecyclerView.Adapter<DepartureListAdapter.DepartureListViewHolder>() {

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
        return DepartureListViewHolder(
            departureLayout
        )
    }

    override fun getItemCount() = services.size

    override fun onBindViewHolder(
        holder: DepartureListViewHolder,
        position: Int
    ) {

        val platform = if (services[position].platform.isNullOrEmpty()) "TBD" else services[position].platform
        val destinations = services[position].destination.locations

        holder.itemView.transitionName = "${holder.itemView.context.getString(R.string.departure_background_transition)}-$position"

        holder.departureDestinationName.text = destinations[destinations.size - 1].locationName?.parseEncoded()
        holder.departureDestinationName.transitionName = "${holder.itemView.context.getString(R.string.departure_text_transition)}-$position"

        holder.platformName.text = holder.containerView?.context?.getString(R.string.platform_prefix, platform)
        services[position].platformIsHidden?.let {
            if (it) {
                holder.platformName.text = (holder.platformName.text as String?)?.plus(" (Predicted)")
            }
        }
        holder.scheduledDepartureTime.text = services[position].scheduledDeparture
        holder.tocName.text = services[position].operator
        setPillColor(holder.tocName.text.toString(), holder)

        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
        val output = SimpleDateFormat("HH:mm", Locale.ENGLISH)
        val std = sdf.parse(services[position].scheduledDeparture!!)
        services[position].estimatedDeparture?.let {
            val etd = sdf.parse(it)
            if (etd!!.after(std)) {
                holder.scheduledDepartureTime.paintFlags =
                    (holder.scheduledDepartureTime.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG))
                holder.estimatedDepartureTime
                    .setTextColor(holder.itemView.resources.getColor(R.color.colorRed, null))
                holder.estimatedDepartureTime.text = output.format(etd)
            } else {
                holder.estimatedDepartureTime
                    .setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                holder.estimatedDepartureTime.text = "On Time"
            }
        } ?: run {
            holder.estimatedDepartureTime
                .setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
            holder.estimatedDepartureTime.text = "On Time"
        }

        holder.scheduledDepartureTime.text = output.format(std)

        services[position].length?.let {
            holder.numCoaches.text = String.format(holder.containerView.resources.getString(R.string.num_coaches_text), it)
        } ?: run {
            holder.numCoaches.visibility = View.INVISIBLE
        }

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
            "south western railway" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSwr, null))
            }
            "london overground" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocLondOverground, null))
            }
            "london north eastern railway" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocLner, null))
            }
            "hull trains" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocHullTrains, null))
            }
            "great northern" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGreatNorthern, null))
            }
            "thameslink" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocThameslink, null))
            }
            "greater anglia" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGreaterAnglia, null))
            }
            "crosscountry" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocXC, null))
            }
            "gatwick express" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGatwick, null))
            }
            "southern" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSouthern, null))
            }
            "southeastern" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSoutheastern, null))
            }
            "c2c" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocC2c, null))
            }
            "avanti west coast" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocAvantiWest, null))
            }
            "west midlands trains" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocWestmidlands, null))
            }
            "chiltern railways" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocChiltern, null))
            }
            "east midlands railway" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocEastMid, null))
            }
            "transpennine express" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTranspenine, null))
            }
            "eurostar" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocEuStar, null))
            }
            "heathrow express" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocHeathrow, null))
            }
            "grand central" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGrandCentral, null))
            }
            "transport for wales" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTfw, null))
            }
            "scotrail" -> holder.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocScotrail, null))
            }
            "merseyrail" -> holder.tocName.apply {
                holder.tocName.setTextColor(holder.tocName.resources.getColor(R.color.colorBackground, null))
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocMerseyRail, null))
            }
            else -> holder.tocName.apply {
                holder.tocName.setTextColor(holder.tocName.resources.getColor(android.R.color.black, null))
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.colorAccent, null))
            }
        }
    }

    open class DepartureListViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View
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
        val numCoaches: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.num_coaches)
        }
    }

    interface DepartureItemClickListener {
        fun onClick(position: Int, itemBackground: View, destinationText: MaterialTextView)
    }

}