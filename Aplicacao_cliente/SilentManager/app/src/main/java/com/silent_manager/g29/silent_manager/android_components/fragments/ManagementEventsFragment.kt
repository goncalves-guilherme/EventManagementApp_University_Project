package com.silent_manager.g29.silent_manager.android_components.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.activities.*
import com.silent_manager.g29.silent_manager.android_components.adapters.InvitationsAdapter
import com.silent_manager.g29.silent_manager.android_components.helpers.HelperMethods
import com.silent_manager.g29.silent_manager.android_components.view_models.ManagementEventsViewModel
import com.silent_manager.g29.silent_manager.data_layer.models.Invite
import kotlinx.android.synthetic.main.fragment_management_events.*


class ManagementEventsFragment : Fragment() {
    companion object {
        const val MANAGMENT_EVENTS_FRAGMENT_TAG = "Management_Events_frag"
    }

    private fun getViewModelFactory() = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ManagementEventsViewModel(activity?.application as SilentManagerApplication) as T
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_management_events, container, false)
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init() {
        accepted_events_btm.setOnClickListener { onEventsIJoinClick() }
        edit_your_events_btm.setOnClickListener { onEventsICreateClick() }
        management_events_logout_txt.setOnClickListener { onLogoutClick() }

        initInvitationsList()
    }

    private fun onLogoutClick() {
        getViewModel().logout()
        fragmentManager?.let {
            HelperMethods.changeFragmentContext(it, LogInFragment(), false)
        }
    }

    private fun invitationsToShow(invitationsToShow: Boolean) {
        if (invitationsToShow) {
            invitations_no_invitations_txt.visibility = View.GONE
            invitations_list.visibility = View.VISIBLE
        } else {
            invitations_no_invitations_txt.visibility = View.VISIBLE
            invitations_list.visibility = View.GONE
        }
    }

    private fun initInvitationsList() {
        val viewModel = getViewModel()
        viewModel.getInvitationsLiveData().observe(this, Observer { invites ->
            if (invites != null && invites.isNotEmpty()) {
                invitationsToShow(true)
                updateInvitationsList(invites.toMutableList())
            } else {
                invitationsToShow(false)
            }
        })
        if (viewModel.getInvitationsLiveData().value.isNullOrEmpty())
            viewModel.updatePendingUserInvitations()
    }

    private fun getViewModel(): ManagementEventsViewModel {
        return ViewModelProviders
            .of(this, getViewModelFactory())
            .get(ManagementEventsViewModel::class.java)
    }

    private fun onEventsIJoinClick() {
        changeActivity(AcceptedInvitationsActivity::class.java)
    }

    private fun onEventsICreateClick() {
        changeActivity(YourEventsActivity::class.java)
    }

    private fun <T> changeActivity(activity: Class<T>) {
        val intent = Intent(context, activity)
        startActivity(intent)
    }

    private fun onInviteStateChange(invite: Invite, position: Int, inviteAccepted: Boolean) {
        val viewModel = getViewModel()

        if (inviteAccepted) {
            viewModel.acceptInvitation(invite)
        } else {
            viewModel.rejectInvitation(invite)
        }

        val inviteAdapter = invitations_list.adapter as InvitationsAdapter
        inviteAdapter.removeInvitationFromPosition(position)
        if (inviteAdapter.itemCount <= 0) {
            invitationsToShow(false)
        }
    }

    private fun onInviteClick(invite: Invite) {
        val intent = Intent(this.context, EventDetail::class.java)
        intent.putExtra(EventDetail.EVENT_KEY_DATA, invite.event)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }

    private fun updateInvitationsList(invites: MutableList<Invite>) {
        val viewManager = LinearLayoutManager(activity)
        val viewAdapter =
            InvitationsAdapter(invites, this::onInviteStateChange, this::onInviteClick)

        invitations_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

}
