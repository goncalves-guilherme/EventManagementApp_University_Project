package com.silent_manager.g29.silent_manager.android_components.adapters

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.Invite
import com.silent_manager.g29.silent_manager.data_layer.models.User

class JointEventsAdapter(
    private val invites: MutableList<Invite>,
    private val acceptedInvitedUsers: MutableList<Pair<Int, Array<User>>>,
    private val pendingInvitedUsers: MutableList<Pair<Int, Array<User>>>,
    private val requestUsers: (Int?) -> Unit,
    private val onEventDetailClick: (Event?) -> Unit,
    private val onLeaveInviteClick: (Invite?) -> Unit
) :
    RecyclerView.Adapter<JointEventsAdapter.MyViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    class MyViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val row = LayoutInflater.from(parent.context)
            .inflate(R.layout.event_joined_row, parent, false)

        return MyViewHolder(row)
    }

    private fun addUsers(
        users: Pair<Int, Array<User>>,
        container: MutableList<Pair<Int, Array<User>>>
    ) {
        val inviteU = container.find { it.first == users.first }

        if (inviteU != null)
            container.remove(inviteU)

        container.add(users)
    }

    fun addPeendingInvitedUsers(inviteUsersList: Pair<Int, Array<User>>) {
        addUsers(inviteUsersList, pendingInvitedUsers)
    }

    fun addAcceptedInvitedUsers(inviteUsersList: Pair<Int, Array<User>>) {
        addUsers(inviteUsersList, acceptedInvitedUsers)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val invite = invites[position]

        val eventStartDateTime = holder.rowView.findViewById<TextView>(R.id.event_joined_start_date)
        val eventFinishDateTime = holder.rowView.findViewById<TextView>(R.id.event_joined_end_date)
        val eventTitle = holder.rowView.findViewById<TextView>(R.id.event_joined_title)
        val eventDescription = holder.rowView.findViewById<TextView>(R.id.event_joined_description)
        val usersInvited =
            holder.rowView.findViewById<RecyclerView>(R.id.event_joined_invited_users_list)
        val pendingUsersInvited =
            holder.rowView.findViewById<RecyclerView>(R.id.event_joined_invited_pending_users_list)
        val leaveInvite =
            holder.rowView.findViewById<TextView>(R.id.event_joined_leave_invite_label)


        eventStartDateTime.text = invite.event?.startDate.toString()
        eventFinishDateTime.text = invite.event?.endDate.toString()
        eventTitle.text = invite.event?.name
        eventDescription.text = invite.event?.description

        holder.rowView.setOnClickListener { onEventDetailClick(invite.event) }
        leaveInvite.setOnClickListener {
            invites.remove(invite)
            acceptedInvitedUsers.removeAll { it.first == invite.event?.eventId }
            notifyItemChanged(position)
            onLeaveInviteClick(invite)
        }

        renderUsersList(invite, acceptedInvitedUsers, usersInvited)
        renderUsersList(invite, pendingInvitedUsers, pendingUsersInvited)
    }

    private fun renderUsersList(
        invite: Invite,
        usersContainer: MutableList<Pair<Int, Array<User>>>,
        usersListView: RecyclerView
    ) {
        val users = usersContainer.find { it.first == invite.event?.eventId }

        if (users == null) {
            requestUsers(invite.event?.eventId)
            return
        }
        updateInvitedUsersList(usersListView, users.second)
    }

    private fun updateInvitedUsersList(holder: RecyclerView, users: Array<User>?) {
        val viewManager = LinearLayoutManager(holder.context)
        viewManager.orientation = LinearLayoutManager.HORIZONTAL
        viewManager.reverseLayout = false
        val viewAdapter = InvitedUsersAdapter(users?.toMutableList(), false){}

        holder.apply {
            layoutManager = viewManager
            adapter = viewAdapter
            setRecycledViewPool(viewPool)
        }
    }

    override fun getItemCount() = invites.size
}