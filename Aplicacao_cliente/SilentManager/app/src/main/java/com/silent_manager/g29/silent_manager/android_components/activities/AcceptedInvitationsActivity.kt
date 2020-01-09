package com.silent_manager.g29.silent_manager.android_components.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.adapters.JointEventsAdapter
import com.silent_manager.g29.silent_manager.android_components.view_models.ManagementEventsViewModel
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.Invite
import kotlinx.android.synthetic.main.activity_events_user_join.*
import kotlinx.android.synthetic.main.content_events_user_join.*


class AcceptedInvitationsActivity : AppCompatActivity() {
    private fun getViewModelFactory() = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ManagementEventsViewModel(application as SilentManagerApplication) as T
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_user_join)
        init()
    }

    private fun init() {
        initAcceptedInvitesList()
    }

    private fun initAcceptedInvitesList() {
        val viewModel = getViewModel()
        viewModel.getInvitationsLiveData().observe(this, Observer { invites ->
            if (invites != null && invites.isNotEmpty()) {
                invitationsToShow(true)
                updateInvitationsList(invites.toMutableList())
            } else {
                invitationsToShow(false)
            }
        })

        // Observer called when a list of events it's rendered and participants are requested.
        viewModel.getAcceptedInvitedUsersByEventLiveData().observe(this, Observer {
            it?.let { a ->
                val adapter = (event_join_list.adapter as JointEventsAdapter)
                adapter.addAcceptedInvitedUsers(a.last())
                adapter.notifyDataSetChanged()
            }
        })

        // Observer called when a list of events it's rendered and participants are requested.
        viewModel.getPendingInvitedUsersByEventLiveData().observe(this, Observer {
            it?.let { a ->
                val adapter = (event_join_list.adapter as JointEventsAdapter)
                adapter.addPeendingInvitedUsers(a.last())
                adapter.notifyDataSetChanged()
            }
        })

        viewModel.updateAcceptedInvitations()
    }

    private fun requestInvitedUsersByEvent(eventId: Int?) {
        val viewModel = getViewModel()
        eventId?.let {
            viewModel.updateInvitationsByEvent(eventId)
        }
    }

    private fun invitationsToShow(invitationsToShow: Boolean) {
        if (invitationsToShow) {
            no_acccepted_Events.visibility = View.GONE
            invited_events_list.visibility = View.VISIBLE
        } else {
            no_acccepted_Events.visibility = View.VISIBLE
            invited_events_list.visibility = View.GONE
        }
    }

    private fun onEventClick(event: Event?) {
        event?.let {
            val intent = Intent(this, EventDetail::class.java)
            intent.putExtra(EventDetail.EVENT_KEY_DATA, event)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        }
    }

    private fun onLeaveEventClick(invite: Invite?) {
        invite?.let {
            getViewModel().rejectInvitation(invite)
        }
    }

    private fun updateInvitationsList(invites: MutableList<Invite>) {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = JointEventsAdapter(
            invites,
            mutableListOf(),
            mutableListOf(),
            ::requestInvitedUsersByEvent,
            ::onEventClick,
            ::onLeaveEventClick
        )

        event_join_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun getViewModel(): ManagementEventsViewModel {
        return ViewModelProviders
            .of(this, getViewModelFactory())
            .get(ManagementEventsViewModel::class.java)
    }

}
