package com.cniekirk.traintimes.ui.adapter

import android.content.res.ColorStateList
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.databinding.DepartureListItemBinding
import com.cniekirk.traintimes.databinding.LoadNextItemBinding
import com.cniekirk.traintimes.databinding.LoadPreviousItemBinding
import com.cniekirk.traintimes.model.Dep
import com.cniekirk.traintimes.model.getdepboard.res.Service
import com.cniekirk.traintimes.model.ui.DepartureItem
import com.cniekirk.traintimes.utils.extensions.parseEncoded
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "DepartureListAdapter"

private const val PREVIOUS_TYPE = 0
private const val DEPARTURE_ITEM_TYPE = 1
private const val MORE_TYPE = 2

class DepartureListAdapter(private val services: List<DepartureItem>,
                           private val previousClickListener: LoadPreviousItemClickListener,
                           private val clickListener: DepartureItemClickListener,
                           private val nextClickListener: LoadNextItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
    ): RecyclerView.ViewHolder {

        return when (viewType) {
            PREVIOUS_TYPE -> {
                Log.i(TAG, "PREVIOUS")
                val departureLayout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.load_previous_item, parent, false)
                LoadPreviousViewHolder(departureLayout)
            }
            DEPARTURE_ITEM_TYPE -> {
                Log.i(TAG, "DEPARTURE")
                val departureLayout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.departure_list_item, parent, false)
                DepartureListViewHolder(departureLayout)
            }
            MORE_TYPE -> {
                Log.i(TAG, "MORE")
                val departureLayout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.load_next_item, parent, false)
                LoadMoreViewHolder(departureLayout)
            }
            else -> {
                Log.i(TAG, "WTF!?!")
                val departureLayout = LayoutInflater.from(parent.context)
                    .inflate(R.layout.departure_list_item, parent, false)
                DepartureListViewHolder(departureLayout)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.i(TAG, "How many: ${services.size}")
        return services.size
    }
    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {

        when {
            getItemViewType(position) == PREVIOUS_TYPE -> {
                val previousHolder = holder as LoadPreviousViewHolder
                previousHolder.itemView.setOnClickListener { previousClickListener.onPreviousClick() }
            }
            getItemViewType(position) == DEPARTURE_ITEM_TYPE -> {

                val departureServices = services.filterIsInstance<DepartureItem.DepartureServiceItem>().map { it.service }
                val depHolder = holder as DepartureListViewHolder

                val platform = if (departureServices[position - 1].platform.isNullOrEmpty()) "TBD" else departureServices[position - 1].platform
                val destinations = departureServices[position - 1].destination.locations

                depHolder.binding.root.transitionName = "${depHolder.itemView.context.getString(R.string.departure_background_transition)}-${position - 1}"

                depHolder.binding.departureDestinationName.text = destinations[destinations.size - 1].locationName?.parseEncoded()
                depHolder.binding.departureDestinationName.transitionName = "${depHolder.itemView.context.getString(R.string.departure_text_transition)}-${position - 1}"

                depHolder.binding.departurePlatformName.text = depHolder.containerView?.context?.getString(R.string.platform_prefix, platform)
                departureServices[position - 1].platformIsHidden?.let {
                    if (it) {
                        holder.binding.departurePlatformName.text = (holder.binding.departurePlatformName.text as String?)?.plus(" (Predicted)")
                    }
                }
                depHolder.binding.scheduledDepartureTime.text = departureServices[position - 1].scheduledDeparture
                depHolder.binding.tocName.text = departureServices[position - 1].operator
                setPillColor(holder.binding.tocName.text.toString(), holder)

                val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.ENGLISH)
                val output = SimpleDateFormat("HH:mm", Locale.ENGLISH)
                val std = sdf.parse(departureServices[position - 1].scheduledDeparture!!)
                departureServices[position - 1].estimatedDeparture?.let {
                    val etd = sdf.parse(it)
                    if (etd!!.after(std)) {
                        depHolder.binding.scheduledDepartureTime.paintFlags =
                            (depHolder.binding.scheduledDepartureTime.paintFlags.or(Paint.STRIKE_THRU_TEXT_FLAG))
                        depHolder.binding.estimatedDepartureTime
                            .setTextColor(depHolder.binding.root.resources.getColor(R.color.colorRed, null))
                        depHolder.binding.estimatedDepartureTime.text = output.format(etd)
                    } else {
                        depHolder.binding.estimatedDepartureTime
                            .setTextColor(depHolder.binding.root.resources.getColor(R.color.colorGreen, null))
                        depHolder.binding.estimatedDepartureTime.text = "On Time"
                    }
                } ?: run {
                    depHolder.binding.estimatedDepartureTime
                        .setTextColor(depHolder.itemView.resources.getColor(R.color.colorGreen, null))
                    depHolder.binding.estimatedDepartureTime.text = "On Time"
                }

                depHolder.binding.scheduledDepartureTime.text = output.format(std)

                departureServices[position - 1].length?.let {
                    depHolder.binding.numCoaches.text = String.format(depHolder.containerView.resources.getString(R.string.num_coaches_text), it)
                } ?: run {
                    depHolder.binding.numCoaches.visibility = View.INVISIBLE
                }

                val serviceItem = services[position] as DepartureItem.DepartureServiceItem
                if (serviceItem.isCircular) {
                    // Show the text
                    depHolder.binding.circularServiceIndicator.visibility = View.VISIBLE
                    depHolder.binding.circularServiceIndicator.text = depHolder.containerView.resources.getString(R.string.circular_service_indication)
                } else {
                    depHolder.binding.circularServiceIndicator.visibility = View.GONE
                }

                depHolder.binding.root.setOnClickListener { clickListener.onClick(position, depHolder.itemView, depHolder.binding.departureDestinationName) }

            }
            getItemViewType(position) == MORE_TYPE -> {
                val nextHolder = holder as LoadMoreViewHolder
                nextHolder.itemView.setOnClickListener { nextClickListener.onNextClick() }
            }
        }
    }

    private fun setPillColor(toc: String, holder: DepartureListViewHolder) {

        holder.binding.tocName.setTextColor(holder.binding.tocName.resources.getColor(android.R.color.white, null))

        when (toc.toLowerCase()) {
            "tfl rail" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTflRail, null))
            }
            "great western railway" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGwr, null))
            }
            "northern" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocNorthern, null))
            }
            "south western railway" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSwr, null))
            }
            "london overground" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocLondOverground, null))
            }
            "london north eastern railway" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocLner, null))
            }
            "hull trains" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocHullTrains, null))
            }
            "great northern" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGreatNorthern, null))
            }
            "thameslink" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocThameslink, null))
            }
            "greater anglia" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGreaterAnglia, null))
            }
            "crosscountry" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocXC, null))
            }
            "gatwick express" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGatwick, null))
            }
            "southern" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSouthern, null))
            }
            "southeastern" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocSoutheastern, null))
            }
            "c2c" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocC2c, null))
            }
            "avanti west coast" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocAvantiWest, null))
            }
            "west midlands trains" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocWestmidlands, null))
            }
            "chiltern railways" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocChiltern, null))
            }
            "east midlands railway" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocEastMid, null))
            }
            "transpennine express" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTranspenine, null))
            }
            "eurostar" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocEuStar, null))
            }
            "heathrow express" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocHeathrow, null))
            }
            "grand central" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocGrandCentral, null))
            }
            "transport for wales" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocTfw, null))
            }
            "scotrail" -> holder.binding.tocName.apply {
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocScotrail, null))
            }
            "merseyrail" -> holder.binding.tocName.apply {
                holder.binding.tocName.setTextColor(holder.binding.tocName.resources.getColor(R.color.colorBackground, null))
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.tocMerseyRail, null))
            }
            else -> holder.binding.tocName.apply {
                holder.binding.tocName.setTextColor(holder.binding.tocName.resources.getColor(android.R.color.black, null))
                (background as GradientDrawable).color = ColorStateList.valueOf(resources.getColor(R.color.colorAccent, null))
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (services[position]) {
            is DepartureItem.LoadBeforeItem -> PREVIOUS_TYPE
            is DepartureItem.DepartureServiceItem -> DEPARTURE_ITEM_TYPE
            is DepartureItem.LoadAfterItem -> MORE_TYPE
        }
    }


    open class DepartureListViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        val binding = DepartureListItemBinding.bind(itemView)

        override val containerView: View
            get() = binding.root
    }

    open class LoadPreviousViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        val binding = LoadPreviousItemBinding.bind(itemView)

        override val containerView: View
            get() = binding.root
    }

    open class LoadMoreViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        val binding = LoadNextItemBinding.bind(itemView)

        override val containerView: View
            get() = binding.root
    }

    interface LoadPreviousItemClickListener {
        fun onPreviousClick()
    }

    interface DepartureItemClickListener {
        fun onClick(position: Int, itemBackground: View, destinationText: MaterialTextView)
    }

    interface LoadNextItemClickListener {
        fun onNextClick()
    }

}