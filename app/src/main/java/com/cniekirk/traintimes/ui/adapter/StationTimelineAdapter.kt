package com.cniekirk.traintimes.ui.adapter

import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.model.getdepboard.res.CallingPoint
import com.cniekirk.traintimes.model.getdepboard.res.Location
import com.cniekirk.traintimes.utils.extensions.parseEncoded
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer
import java.text.SimpleDateFormat
import java.util.*

class StationTimelineAdapter(
    private val callingPoints: List<Location?>,
    private val currentIndex: Int,
    private val onStationItemClickedListener: OnStationItemClickedListener,
    private val isCancelled: Boolean = false
): RecyclerView.Adapter<StationTimelineAdapter.StationTimelineViewHolder>() {

    // TODO: Replace with sealed class for exhaustive when
    companion object {
        const val STATION_START = 0
        const val STATION_MIDDLE = 1
        const val STATION_END = 2
    }

    private lateinit var greenColor: ColorFilter
    private lateinit var sideColor: ColorFilter
    private lateinit var redColor: ColorFilter

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
        redColor = PorterDuffColorFilter(
            parent.resources.getColor(R.color.colorRed, null), PorterDuff.Mode.SRC_IN
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
        callingPoints[position]?.let {

            holder.stationName.text = callingPoints[position]?.locationName?.parseEncoded()

            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.ENGLISH)
            val output = SimpleDateFormat("HH:mm", Locale.ENGLISH)

            val std = if (callingPoints[position]?.std.isNullOrEmpty()) {
                Log.d("WTF", callingPoints[position].toString())
                sdf.parse(callingPoints[position]?.sta!!)
            } else {
                sdf.parse(callingPoints[position]?.std!!)
            }

            if (isCancelled) {

                if (getItemViewType(position) == STATION_MIDDLE) {
                    holder.sideLineTop.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorRed, null))
                    holder.sideLineBottom.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorRed, null))
                } else {
                    holder.sideLine.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorRed, null))
                }
                holder.stationIndicator.background.colorFilter = redColor
                holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorRed, null))
                holder.stationStatus.text = holder.containerView?.context?.getString(R.string.status_cancelled)
            } else {

                if (!callingPoints[position]?.etd.isNullOrEmpty()) {
                    val etd = sdf.parse(callingPoints[position]?.etd!!)
                    holder.stationStatus.text = output.format(etd!!)
                    if (!etd.after(std)) {
                        holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                    } else {
                        if (!output.format(etd).equals(output.format(std), true)) {
                            holder.stationDelay.text = output.format(etd)
                            holder.stationStatus.paintFlags = (holder.stationStatus.paintFlags.or(
                                Paint.STRIKE_THRU_TEXT_FLAG))
                            holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorText, null))
                        } else {
                            holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                        }
                    }
                } else {
//            if (callingPoints[position].actualTime.equals(callingPoints[position].scheduledTime, true)) {
//                Log.e("ADAPT", "ETD: ${callingPoints[position].estimatedTime} STD: ${callingPoints[position].scheduledTime}")
//                holder.stationStatus.text = callingPoints[position].scheduledTime
//            } else {
//                holder.stationStatus.text = "${callingPoints[position].scheduledTime} ${callingPoints[position].actualTime}"
//            }
                    holder.stationStatus.text = output.format(std!!)
                    callingPoints[position]?.atd?.let {
                        val atd = sdf.parse(callingPoints[position]?.atd!!)
                        if (!atd!!.after(std)) {
                            holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                        } else {
                            if (!output.format(atd).equals(output.format(std), true)) {
                                holder.stationDelay.text = output.format(atd)
                                holder.stationStatus.paintFlags = (holder.stationStatus.paintFlags.or(
                                    Paint.STRIKE_THRU_TEXT_FLAG))
                                holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorText, null))
                            } else {
                                holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                            }
                        }
                    } ?: run {
                        holder.stationStatus.setTextColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                    }

                }

                when {
                    position < currentIndex -> {
                        // Make green
                        if (getItemViewType(position) == STATION_MIDDLE) {
                            holder.sideLineTop.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                            holder.sideLineBottom.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                        } else {
                            holder.sideLine.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                        }
                        holder.stationIndicator.background.colorFilter = greenColor
                    }
                    position == currentIndex -> {
                        // Make grey
                        if (callingPoints[position]?.departureType.equals("forecast", true)) {
                            if (getItemViewType(position) == STATION_MIDDLE) {
                                holder.sideLineTop.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                                holder.sideLineBottom.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorUnselected, null))
                            } else {
                                holder.sideLine.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorUnselected, null))
                            }
                        } else {
                            if (getItemViewType(position) == STATION_MIDDLE) {
                                holder.sideLineTop.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                                holder.sideLineBottom.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                            } else {
                                holder.sideLine.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorGreen, null))
                            }
                        }
                        holder.stationIndicator.background.colorFilter = greenColor

                        if (callingPoints[position]?.arrivalType.equals("forecast", true)) {
                            holder.sideLineTop.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorUnselected, null))
                            holder.sideLineBottom.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorUnselected, null))
                            holder.stationIndicator.background.colorFilter = sideColor
                        }
                    }
                    position > currentIndex -> {
                        // Make grey
                        if (getItemViewType(position) == STATION_MIDDLE) {
                            holder.sideLineTop.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorUnselected, null))
                            holder.sideLineBottom.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorUnselected, null))
                        } else {
                            holder.sideLine.setBackgroundColor(holder.itemView.resources.getColor(R.color.colorUnselected, null))
                        }
                        holder.stationIndicator.background.colorFilter = sideColor
                    }
                }

            }

            holder.stationPlatform.visibility = View.VISIBLE
            it.platform?.let { platform ->
                holder.stationPlatform.text = "P$platform"
                it.platformIsHidden?.let { isHidden ->
                    if (isHidden) {
//                        holder.stationPlatform.text = holder.stationPlatform.text as String + " ?"
                        holder.stationPlatform.alpha = 0.5f
                    } else {
                        holder.stationPlatform.alpha = 1f
                    }
                } ?: run {
                    holder.stationPlatform.alpha = 1f
                }
            } ?: run {
                holder.stationPlatform.visibility = View.GONE
            }

            holder.containerView?.setOnClickListener { onStationItemClickedListener.onStationItemClicked(callingPoints[position]!!) }

        }

    }

    open class StationTimelineViewHolder(itemView: View)
        :RecyclerView.ViewHolder(itemView), LayoutContainer {

        override val containerView: View?
            get() = itemView

        val sideLine: View by lazy {
            itemView.findViewById<View>(R.id.side_bar)
        }

        val sideLineTop: View by lazy {
            itemView.findViewById<View>(R.id.side_bar_top)
        }

        val sideLineBottom: View by lazy {
            itemView.findViewById<View>(R.id.side_bar_btm)
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

        val stationDelay: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.stop_delay_time)
        }

        val stationPlatform: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.platform_indicator)
        }

    }

    interface OnStationItemClickedListener {
        fun onStationItemClicked(station: Location)
    }

}