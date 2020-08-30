package com.cniekirk.traintimes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.model.getdepboard.local.Query
import com.google.android.material.textview.MaterialTextView
import kotlinx.android.extensions.LayoutContainer

class RecentQueriesAdapter(private val queries: List<Query>,
                            private val onClickListener: RecentQueryClickListener)
    :RecyclerView.Adapter<RecentQueriesAdapter.RecentQueriesViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentQueriesViewHolder {
        return RecentQueriesViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.recent_query_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecentQueriesViewHolder, position: Int) {

        val query = queries[position]

        query.toName?.let {
            holder.startStation.text = query.fromName
            holder.endStation.text = it
            holder.endStation.visibility = View.VISIBLE
            holder.endTitle.visibility = View.VISIBLE
        } ?: run {
            holder.startStation.text = query.fromName
            holder.endStation.visibility = View.GONE
            holder.endTitle.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            onClickListener.onClick(position)
        }

    }

    override fun getItemCount() = queries.size

    open class RecentQueriesViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {
        override val containerView: View
            get() = itemView

        val startStation: MaterialTextView by lazy {
            itemView.findViewById(R.id.start_station)
        }
        val endStation: MaterialTextView by lazy {
            itemView.findViewById(R.id.end_station)
        }
        val endTitle: MaterialTextView by lazy {
            itemView.findViewById(R.id.to_text)
        }
    }

    interface RecentQueryClickListener {
        fun onClick(position: Int)
    }

}