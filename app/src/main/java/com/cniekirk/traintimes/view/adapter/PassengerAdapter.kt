package com.cniekirk.traintimes.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer

class PassengerAdapter(private val passengerTypes: List<String>,
                       private val onPassengerClickedListener: OnPassengerClickedListener)
    : RecyclerView.Adapter<PassengerAdapter.PassengerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PassengerViewHolder {
        val passengerLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_railcard, parent, false)
        return PassengerViewHolder(passengerLayout)
    }

    override fun getItemCount() = passengerTypes.size

    override fun onBindViewHolder(holder: PassengerViewHolder, position: Int) {
        holder.passengerName.text = passengerTypes[position]
        holder.itemView.setOnClickListener {
            if (holder.passengerCount.text == holder.itemView.context.getString(R.string.increment)) {
                holder.passengerCount.text = "X 1"
                onPassengerClickedListener.onPassengerClick(position)
            } else {
                val lastChar = holder.passengerCount.text[holder.passengerCount.text.lastIndex]
                val count = Character.getNumericValue(lastChar) + 1
                holder.passengerCount.text = "X $count"
                if (count < 8)
                    onPassengerClickedListener.onPassengerClick(position)
            }
        }
        if (position == 0) {
            holder.passengerCount.text = "X 1"
        }
    }

    open class PassengerViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View
            get() = itemView

        val passengerName: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.railcard_name)
        }
        val passengerCount: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.railcard_count)
        }
    }

    interface OnPassengerClickedListener {
        fun onPassengerClick(position: Int)
    }

}