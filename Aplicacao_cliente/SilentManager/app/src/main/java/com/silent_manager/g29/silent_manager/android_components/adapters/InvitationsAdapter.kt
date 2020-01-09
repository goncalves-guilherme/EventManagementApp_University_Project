package com.silent_manager.g29.silent_manager.android_components.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.helpers.HelperMethods
import com.silent_manager.g29.silent_manager.data_layer.models.Invite
import kotlinx.android.synthetic.main.invitations_row.view.*

class InvitationsAdapter(
    private val invites: MutableList<Invite>,
    private val onInviteStateChange: (Invite, Int, Boolean) -> Unit,
    private val onInviteDetailClick: (Invite) -> Unit
) :
    RecyclerView.Adapter<InvitationsAdapter.MyViewHolder>() {

    class MyViewHolder(val rowView: View) : RecyclerView.ViewHolder(rowView)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val row = LayoutInflater.from(parent.context)
            .inflate(R.layout.invitations_row, parent, false)

        return MyViewHolder(row)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val invite = invites[position]

        val eventStartDateTime = holder.rowView.findViewById<TextView>(R.id.invitation_start_at)
        val eventFinishDateTime = holder.rowView.findViewById<TextView>(R.id.invitation_finish_at)
        val eventTitle = holder.rowView.findViewById<TextView>(R.id.invitation_event_title_txt)
        val eventAuthor = holder.rowView.findViewById<TextView>(R.id.invitation_created_by_txt)

        eventStartDateTime.text = HelperMethods.getDateFormatted(invite.event?.startDate)
        eventFinishDateTime.text = HelperMethods.getDateFormatted(invite.event?.endDate)
        eventTitle.text = invite.event?.name
        eventAuthor.text = invite.event?.author?.name

        holder.rowView.setOnClickListener {
            onInviteDetailClick(invite)
        }

        holder.rowView.invitation_accepted_btm.setOnClickListener {
            onInviteStateChange(
                invite,
                position,
                true
            )
        }
        holder.rowView.invitation_rejected_btm.setOnClickListener {
            onInviteStateChange(
                invite,
                position,
                false
            )
        }
    }

    fun removeInvitationFromPosition(position: Int) {
        invites.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    override fun getItemCount() = invites.size
}