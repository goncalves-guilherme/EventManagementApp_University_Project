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
import com.silent_manager.g29.silent_manager.android_components.adapters.CreatedEventsAdapter
import com.silent_manager.g29.silent_manager.android_components.view_models.EventsCreatedViewModel
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.User
import kotlinx.android.synthetic.main.content_events_created.*
import kotlinx.android.synthetic.main.content_loading_data.*

class YourEventsActivity : AppCompatActivity() {
    private fun getViewModelFactory() = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return EventsCreatedViewModel(application as SilentManagerApplication) as T
        }
    }

    companion object {
        const val ON_USER_INVITED = 3901
        const val UPDATED_EVENT_REQUEST_CODE = 322
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_events_created)
        activateLoaderScreen(true)
        init()
    }

    private fun noEventsCreated(noEventsCreated: Boolean) {
        if (!noEventsCreated) {
            events_created_no_events_yet_txt.visibility = View.GONE
            created_events_list_containner.visibility = View.VISIBLE
            return
        }

        events_created_no_events_yet_txt.visibility = View.VISIBLE
        created_events_list_containner.visibility = View.GONE

    }

    private fun activateLoaderScreen(activate: Boolean) {
        if (activate) {
            loading_content_container.visibility = View.VISIBLE
            created_events_containner.visibility = View.GONE
        } else {
            loading_content_container.visibility = View.GONE
            created_events_containner.visibility = View.VISIBLE
        }
    }

    private fun init() {
        noEventsCreated(false)
        val vm = getViewModel()
        vm.getCreatedEvents().observe(this, Observer {
            activateLoaderScreen(false)
            if (it != null && it.isNotEmpty()) {
                updateCreatedEventsList(it)
                noEventsCreated(false)
            } else {
                noEventsCreated(true)
            }
        })

        vm.getInvitedAcceptedUsersByEventLiveData().observe(this, Observer {
            it?.let { a ->
                val adapter = (created_events_list_containner.adapter as CreatedEventsAdapter)
                adapter.addAcceptedInvitedUsers(a.last())
            }
        })

        vm.getInvitedPendingUsersByEventLiveData().observe(this, Observer {
            it?.let { a ->
                val adapter = (created_events_list_containner.adapter as CreatedEventsAdapter)
                adapter.addPendingInviteUsers(a.last())
            }
        })

        events_created_create_new_event_btm.setOnClickListener { createNewEvent() }
        if (vm.getCreatedEvents().value.isNullOrEmpty())
            vm.updateEvents()
    }

    private fun createNewEvent() {
        val intent = Intent(this, CreateEventActivity::class.java)
        startActivity(intent)
    }

    private fun goToSearchUsers(eventId: Int?, invitedUsersNames: Array<String>?) {
        val intent = Intent(this, SearchUsersActivity::class.java)

        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(SearchUsersActivity.INVITED_USERS_NAMES_LIST, invitedUsersNames)
        intent.putExtra(SearchUsersActivity.INVITED_EVENT_ID, eventId)

        startActivityForResult(intent, ON_USER_INVITED)
    }

    private fun onInviteClick(event: Event, users: Array<String>) {
        goToSearchUsers(event.eventId, users)
    }

    private fun onDeleteClick(event: Event, position: Int) {
        event.eventId?.let { id ->
            getViewModel().deleteEvent(id)
            val adapter = created_events_list_containner.adapter as CreatedEventsAdapter
            adapter.removedEvent(position)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            UPDATED_EVENT_REQUEST_CODE -> {
                val event =
                    data?.getSerializableExtra(CreateEventActivity.EVENT_TO_UPDATE_KEY) as? Event
                val adapter = created_events_list_containner.adapter as CreatedEventsAdapter
                adapter.updateEvent(event)
            }
            ON_USER_INVITED -> {
                val user =
                    data?.getSerializableExtra(SearchUsersActivity.USER_KEY) as? User
                val eventId = data?.getIntExtra(SearchUsersActivity.INVITED_EVENT_ID, -1)

                if (eventId != null && eventId > 0 && user != null && user.id != null) {
                    getViewModel().inviteUser(eventId, user.id)
                }
            }
        }
    }

    private fun onInviteRemove(eventId: Int?, userId: Int?) {
        if (eventId != null && userId != null) {
            getViewModel().deleteInvite(eventId, userId)
        }
    }

    private fun onClickEditEvent(event: Event) {
        val intent = Intent(this, CreateEventActivity::class.java)
        intent.putExtra(CreateEventActivity.EVENT_TO_UPDATE_KEY, event)
        startActivityForResult(intent, UPDATED_EVENT_REQUEST_CODE)
    }

    private fun onClickEvent(event: Event) {
        val intent = Intent(this, EventDetail::class.java)
        intent.putExtra(EventDetail.EVENT_KEY_DATA, event)
        startActivity(intent)
    }

    private fun requestInvitedUsersByEvent(eventId: Int?) {
        val viewModel = getViewModel()
        eventId?.let {
            viewModel.updateInvitationsByEvent(eventId)
        }
    }

    private fun updateCreatedEventsList(events: MutableList<Event>) {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter =
            CreatedEventsAdapter(
                events,
                mutableListOf(),
                mutableListOf(),
                ::onClickEditEvent,
                ::onInviteClick,
                ::onDeleteClick,
                ::requestInvitedUsersByEvent,
                ::onClickEvent,
                ::onInviteRemove
            )

        created_events_list_containner.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun getViewModel(): EventsCreatedViewModel {
        return ViewModelProviders
            .of(this, getViewModelFactory())
            .get(EventsCreatedViewModel::class.java)
    }
}