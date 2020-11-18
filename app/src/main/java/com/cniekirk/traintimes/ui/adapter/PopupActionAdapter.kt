package com.cniekirk.traintimes.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.cniekirk.traintimes.R
import com.cniekirk.traintimes.databinding.ItemPopupActionBinding
import com.cniekirk.traintimes.model.ui.PopupAction
import kotlinx.android.extensions.LayoutContainer

class PopupActionAdapter(private val actions: List<PopupAction>):
    RecyclerView.Adapter<PopupActionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_popup_action, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.actionText.text = actions[position].actionName
        holder.binding.actionIcon.setImageDrawable(ResourcesCompat.getDrawable(
            holder.containerView.resources, actions[position].actionIcon, null))
    }

    override fun getItemCount() = actions.size

    open class ViewHolder(itemView: View):
        RecyclerView.ViewHolder(itemView), LayoutContainer {

        val binding = ItemPopupActionBinding.bind(itemView)

        override val containerView = binding.root

    }

}