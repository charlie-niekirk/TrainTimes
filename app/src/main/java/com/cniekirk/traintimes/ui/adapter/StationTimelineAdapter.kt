package com.cniekirk.traintimes.ui.adapter

import android.graphics.ColorFilter
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.model.getdepboard.res.CallingPoint
import com.cniekirk.traintimes.utils.extensions.parseEncoded
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer

class StationTimelineAdapter(
    private val callingPoints: List<CallingPoint>,
    private val currentIndex: Int,
    private val onStationItemClickedListener: OnStationItemClickedListener
): RecyclerView.Adapter<StationTimelineAdapter.StationTimelineViewHolder>() {

    // TODO: Replace with sealed class for exhaustive when
    companion object {
        const val STATION_START = 0
        const val STATION_MIDDLE = 1
        const val STATION_END = 2
    }

    private lateinit var greenColor: ColorFilter
    private lateinit var sideColor: ColorFilter

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StationTimelineViewHolder {

        greenColor = PorterDuffColorFilter(
            parent.resources.getColor(R.color.colorGreen, null), PorterDuff.Mode.SRC_IN
        )
        sideColor = PorterDuffColorFilter(
            parent.resources.getColor(R.color.colorUnselected, null), PorterDuff.Mode.SRC_IN
        )

        val stationTimelineLayout = when (viewType) {
            STATION_START -> LayoutInflater.from(parent.context)
                .inflate(R.layout.station_stop_first_item, parent, false)
            STATION_MIDDLE -> LayoutInflater.from(parent.context)
                .inflate(R.layout.station_stop_middle_item, parent, false)
            STATION_END -> LayoutInflater.from(parent.context)
                .inflate(R.layout.station_stop_end_item, parent, false)
            else -> LayoutInflater.from(parent.context)
                .inflate(R.layout.station_stop_middle_item, parent, false)
        }
        return StationTimelineViewHolder(stationTimelineLayout)
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> STATION_START
            callingPoints.size - 1 -> STATION_END
            else -> STATION_MIDDLE
        }
    }

    override fun getItemCount(): Int {
        return callingPoints.size
    }

    override fun onBindViewHolder(
        holder: StationTimelineViewHolder,
        position: Int
    ) {
        holder.stationName.text = callingPoints[position].locationName.parseEncoded()

        if (!callingPoints[position].estimatedTime.isNullOrEmpty()) {
            holder.stationStatus.text = callingPoints[position].scheduledTime
            if (callingPoints[position].estimatedTime.equals("On Time", ignoreCase = true) or
                callingPoints[position].estimatedTime.equals("Departed", ignoreCase = true)) {
                holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
            } else {
                holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorRed, null))
                holder.stationStatus.text = callingPoints[position].estimatedTime
            }
        } else {
//            if (callingPoints[position].actualTime.equals(callingPoints[position].scheduledTime, true)) {
//                Log.e("ADAPT", "ETD: ${callingPoints[position].estimatedTime} STD: ${callingPoints[position].scheduledTime}")
//                holder.stationStatus.text = callingPoints[position].scheduledTime
//            } else {
//                holder.stationStatus.text = "${callingPoints[position].scheduledTime} ${callingPoints[position].actualTime}"
//            }
            holder.stationStatus.text = callingPoints[position].scheduledTime
            if (callingPoints[position].actualTime.equals("On Time", ignoreCase = true) or
                callingPoints[position].actualTime.equals("Departed", ignoreCase = true)) {
                holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
            } else {
                holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorRed, null))
                holder.stationStatus.text = callingPoints[position].estimatedTime
            }
        }

        if (position <= currentIndex) {
            // Make green
            holder.sideLine.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
            holder.stationIndicator.background.colorFilter = greenColor
        } else if (position > currentIndex) {
            // Make grey
            holder.sideLine.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorUnselected, null))
            holder.stationIndicator.background.colorFilter = sideColor
        }

        holder.containerView?.setOnClickListener { onStationItemClickedListener.onStationItemClicked(callingPoints[position]) }
    }

    open class StationTimelineViewHolder(itemView: View)
        :RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        val sideLine: View by lazy {
            itemView.findViewById<View>(R.id.side_bar)
        }

        val stationIndicator: View by lazy {
            itemView.findViewById<View>(R.id.station_indicator)
        }

        val stationName: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.station_name)
        }

        val stationStatus: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.stop_status)
        }

    }

    interface OnStationItemClickedListener {
        fun onStationItemClicked(station: CallingPoint)
    }

}