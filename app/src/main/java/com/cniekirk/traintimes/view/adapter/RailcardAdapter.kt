package com.cniekirk.traintimes.view.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer

class RailcardAdapter(private val railcards: List<String>,
                      private val railcardClickListener: RailcardClickListener)
    : RecyclerView.Adapter<RailcardAdapter.RailcardViewHolder>() {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(position: Int): Long {
        val item = railcards[position]
        return item.hashCode().toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RailcardViewHolder {
        val railcardLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_railcard, parent, false)
        return RailcardViewHolder(railcardLayout)
    }

    override fun getItemCount() = railcards.size

    override fun onBindViewHolder(holder: RailcardViewHolder, position: Int) {

        holder.railcardName.text = railcards[position]
        holder.itemView.setOnClickListener {
            if (holder.railcardCount.text == holder.itemView.context.getString(R.string.increment)) {
                holder.railcardCount.text = "X 1"
                railcardClickListener.onClick(position)
            } else {
                val lastChar = holder.railcardCount.text[holder.railcardCount.text.lastIndex]
                val count = Character.getNumericValue(lastChar) + 1
                holder.railcardCount.text = "X $count"
                if (count < 8)
                    railcardClickListener.onClick(position)
            }
        }

    }

    open class RailcardViewHolder(itemView: View):
            RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View
            get() = itemView

        val railcardName: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.railcard_name)
        }
        val railcardCount: MaterialTextView by lazy {
            itemView.findViewById<MaterialTextView>(R.id.railcard_count)
        }
    }

    interface RailcardClickListener {
        fun onClick(position: Int)
    }
}