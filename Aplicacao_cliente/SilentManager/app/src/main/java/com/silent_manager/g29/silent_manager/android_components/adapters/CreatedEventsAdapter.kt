package com.silent_manager.g29.silent_manager.android_components.adapters

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.helpers.HelperMethods
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.User

class CreatedEventsAdapter(
    private val events: MutableList<Event>,
    private val invitedAcceptedUsers: MutableList<Pair<Int, MutableList<User>>>,
    private val invitePendingUsers: MutableList<Pair<Int, MutableList<User>>>,
    private val onClickEditEvent: (Event) -> Unit,
    private val onClickInvite: (Event, Array<String>) -> Unit,
    private val onClickDelete: (Event, Int) -> Unit,
    private val requestUsers: (Int?) -> Unit,
    private val onClickEvent: (Event) -> Unit,
    private val onInviteRemoveClick: (Int?, Int?) -> Unit
) :
    RecyclerView.Adapter<CreatedEventsAdapter.MyViewHolder>() {

    class MyViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView)

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val row = LayoutInflater.from(parent.context)
            .inflate(R.layout.created_event_row, parent, false)

        return MyViewHolder(row)
    }

    fun updateEvent(event: Event?) {
        events.removeAll { it.eventId == event?.eventId }
        event?.let {
            events.add(it)
            notifyDataSetChanged()
        }
    }

    fun addAcceptedInvitedUsers(inviteUsersList: Pair<Int, Array<User>>) {
        addUsers(invitedAcceptedUsers, inviteUsersList)
        notifyDataSetChanged()
    }

    fun addPendingInviteUsers(inviteUsersList: Pair<Int, Array<User>>) {
        addUsers(invitePendingUsers, inviteUsersList)
        notifyDataSetChanged()
    }

    private fun addUsers(
        usersContainer: MutableList<Pair<Int, MutableList<User>>>,
        usersToAdd: Pair<Int, Array<User>>
    ) {
        val inviteU = usersContainer.find { it.first == usersToAdd.first }

        if (inviteU != null)
            usersContainer.remove(inviteU)

        usersContainer.add(
            Pair(
                usersToAdd.first,
                usersToAdd.second.toMutableList()
            )
        )
    }

    fun removedEvent(position: Int) {
        events.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val event = events[position]

        val eventDateTime =
            holder.rowView.findViewById<TextView>(R.id.created_events_row_date_time_label)
        val eventTitle = holder.rowView.findViewById<TextView>(R.id.created_events_row_title_txt)
        val eventDesc =
            holder.rowView.findViewById<TextView>(R.id.created_events_row_description_txt)
        val invitedUsersView =
            holder.rowView.findViewById<RecyclerView>(R.id.created_event_users_invited_list)
        val pendingInvitedUsersView =
            holder.rowView.findViewById<RecyclerView>(
                R.id.created_event_pending_users_invited_list
            )
        val noAcceptedUsers =
            holder.rowView.findViewById<TextView>(R.id.no_accepted_users_lbl)

        val noPendingUsers =
            holder.rowView.findViewById<TextView>(R.id.no_pending_users_lbl)

        eventDateTime.text = HelperMethods.getDateFormatted(event.startDate)
        eventTitle.text = event.name
        eventDesc.text = event.description

        holder.rowView.setOnClickListener {
            onClickEvent(event)
        }

        holder.rowView.findViewById<TextView>(R.id.created_events_edit_btm)
            .setOnClickListener {
                onClickEditEvent(event)
            }

        holder.rowView.findViewById<TextView>(R.id.created_events_row_invite_btm)
            .setOnClickListener {
                onInviteClick(event)
            }

        holder.rowView.findViewById<TextView>(R.id.created_events_delete_btm).setOnClickListener {
            onClickDelete(event, position)
        }

        // Update accepted users lists
        updateInvitedUsersList(invitedUsersView, noAcceptedUsers, event, invitedAcceptedUsers)
        // Update pending users lists
        updateInvitedUsersList(pendingInvitedUsersView, noPendingUsers, event, invitePendingUsers)
    }

    private fun onInviteClick(event: Event) {
        // Get all pending and accepted users
        val acceptedUsers =
            invitedAcceptedUsers.find { i -> i.first == event.eventId }
                ?.second?.mapNotNull { u -> u.name }?.toTypedArray()

        val pendingUsers =
            invitePendingUsers.find { i -> i.first == event.eventId }
                ?.second?.mapNotNull { u -> u.name }?.toTypedArray()

        // Join all of the users
        val users: MutableList<String> = mutableListOf()
        acceptedUsers?.let { users.addAll(it) }
        pendingUsers?.let { users.addAll(it) }

        // Send to the other activity to use as a filter in user's search activity
        onClickInvite(event, users.toTypedArray())
    }

    private fun updateInvitedUsersList(
        holder: RecyclerView,
        noUsersFoundView: TextView,
        event: Event,
        usersContainer: MutableList<Pair<Int, MutableList<User>>>
    ) {
        val users = usersContainer.find { it.first == event.eventId }

        if (users == null) {
            requestUsers(event.eventId)
            return
        }

        if (users.second.isNullOrEmpty()) {
            holder.visibility = View.GONE
            noUsersFoundView.visibility = View.VISIBLE
            return
        }

        holder.visibility = View.VISIBLE
        noUsersFoundView.visibility = View.GONE

        val viewManager = LinearLayoutManager(holder.context)
        viewManager.orientation = LinearLayoutManager.HORIZONTAL
        viewManager.reverseLayout = false

        val viewAdapter =
            InvitedUsersAdapter(users.second, true) {
                onInviteRemoveClick(event.eventId, it)
            }

        holder.apply {
            layoutManager = viewManager
            adapter = viewAdapter
            setRecycledViewPool(viewPool)
        }
    }

    override fun getItemCount() = events.size
}