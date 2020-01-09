package com.silent_manager.g29.silent_manager.android_components.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.helpers.HelperMethods
import com.silent_manager.g29.silent_manager.data_layer.models.Event

class HomeEventAdapter(
    private val events: MutableList<Event>,
    private val onClickEvent: (Event) -> Unit
) :
    RecyclerView.Adapter<HomeEventAdapter.MyViewHolder>() {

    class MyViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val row = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_row, parent, false)

        return MyViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = events[position]

        val eventDateTime = holder.rowView.findViewById<TextView>(R.id.created_events_row_date_time_txt)
        val eventTitle = holder.rowView.findViewById<TextView>(R.id.created_events_row_title_txt)
        val eventDesc = holder.rowView.findViewById<TextView>(R.id.event_row_description_txt)
        val eventAuthor = holder.rowView.findViewById<TextView>(R.id.event_row_created_by_txt)

        eventDateTime.text = HelperMethods.getDateFormatted(event.startDate)
        eventTitle.text = event.name
        eventDesc.text = event.description
        eventAuthor.text = event.author?.name

        holder.rowView.setOnClickListener { onClickEvent(event) }
    }

    override fun getItemCount() = events.size
}