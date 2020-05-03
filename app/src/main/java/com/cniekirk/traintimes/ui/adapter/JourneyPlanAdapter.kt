package com.cniekirk.traintimes.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.databinding.JourneyPlanItemBinding
import com.cniekirk.traintimes.model.journeyplanner.res.OutwardJourney
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.math.absoluteValue

class JourneyPlanAdapter(private val journeys: MutableList<OutwardJourney>,
                         private val shouldShowPrice: Boolean):
    RecyclerView.Adapter<JourneyPlanAdapter.JourneyViewHolder>() {

    override fun getItemCount() = journeys.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JourneyViewHolder {
        Log.d("Adapter", "Size: ${journeys.size}")
        val itemBinding = JourneyPlanItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JourneyViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: JourneyViewHolder, position: Int) {
        val cheapest = journeys.minBy { outwardJourney -> outwardJourney.fare!![0].totalPrice!!.toInt() }
        val fastest = fastest(journeys)
        if (fastest == position) {
            holder.bindData(journeys[position], shouldShowPrice, (cheapest?.id == journeys[position].id), true)
        } else {
            holder.bindData(journeys[position], shouldShowPrice, (cheapest?.id == journeys[position].id), false)
        }
    }

    private fun fastest(journeys: List<OutwardJourney>): Int {
        var timeDifference = 0L
        var fastest = 0
        journeys.forEachIndexed { i, journey ->
            journey.timetable?.realtime?.let { realtime ->
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH)
                val depart = LocalDateTime.parse(realtime.departure, formatter)
                val arrive = LocalDateTime.parse(realtime.arrival, formatter)
                val journeyDuration = Duration.between(depart, arrive)
                if ((timeDifference == 0L) or
                    (journeyDuration.seconds < timeDifference)) {
                    timeDifference = journeyDuration.seconds
                    fastest = i
                }
            } ?: run {
                journey.timetable?.scheduled?.let { scheduled ->
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH)
                    val depart = LocalDateTime.parse(scheduled.departure, formatter)
                    val arrive = LocalDateTime.parse(scheduled.arrival, formatter)
                    val journeyDuration = Duration.between(depart, arrive)
                    if ((timeDifference == 0L) or
                        (journeyDuration.seconds < timeDifference)) {
                        timeDifference = journeyDuration.seconds
                        fastest = i
                    }
                }
            }
        }
        return fastest
    }

    open class JourneyViewHolder(private val itemBinding: JourneyPlanItemBinding):
            RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(outwardJourney: OutwardJourney, shouldShowPrice: Boolean, isCheapest: Boolean, isFastest: Boolean) {

            itemBinding.departStation.text = outwardJourney.origin
            itemBinding.destinationStation.text = outwardJourney.destination
            outwardJourney.timetable?.realtime?.let {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH)
                val depart = LocalDateTime.parse(it.departure, formatter)
                val arrive = LocalDateTime.parse(it.arrival, formatter)
                itemBinding.departTime.text = depart.format(DateTimeFormatter.ofPattern("HH:mm"))
                itemBinding.destinationTime.text = arrive.format(DateTimeFormatter.ofPattern("HH:mm"))
                val journeyDuration = Duration.between(depart, arrive)
                val hours = journeyDuration.toHours()
                val minutes = journeyDuration.minusHours(hours).toMinutes()
                itemBinding.journeyDuration.text = itemBinding.root.context.getString(R.string.journey_duration_format, hours, minutes)
            } ?: run {
                outwardJourney.timetable?.scheduled?.let {
                    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ", Locale.ENGLISH)
                    val depart = LocalDateTime.parse(it.departure, formatter)
                    val arrive = LocalDateTime.parse(it.arrival, formatter)
                    itemBinding.departTime.text = depart.format(DateTimeFormatter.ofPattern("HH:mm"))
                    itemBinding.destinationTime.text = arrive.format(DateTimeFormatter.ofPattern("HH:mm"))
                    val journeyDuration = Duration.between(depart, arrive)
                    val hours = journeyDuration.toHours()
                    val minutes = journeyDuration.minusHours(hours).toMinutes()
                    itemBinding.journeyDuration.text = itemBinding.root.context.getString(R.string.journey_duration_format, hours, minutes)
                }
            }
            itemBinding.journeyChanges.text = itemBinding.root.context.getString(R.string.journey_changes_format, outwardJourney.leg?.size)
            outwardJourney.fare?.let {
                if (shouldShowPrice) {

                    itemBinding.journeyPrice.visibility = View.VISIBLE
                    itemBinding.journeyPrice.text = "£%.2f".format(outwardJourney.fare[0].totalPrice?.toFloat()
                        ?.div(100f))
                }
                itemBinding.ticketType.text = outwardJourney.fare[0].description
            }
            if (isCheapest) {
                itemBinding.cheapestIndicator.visibility = View.VISIBLE
            } else {
                itemBinding.cheapestIndicator.visibility = View.GONE
            }
            if (isFastest) {
                itemBinding.fastestIndicator.visibility = View.VISIBLE
            } else {
                itemBinding.fastestIndicator.visibility = View.GONE
            }
        }

    }

}