package com.silent_manager.g29.silent_manager.android_components.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.activities.ShowScreenRules
import com.silent_manager.g29.silent_manager.android_components.adapters.HomeEventAdapter
import com.silent_manager.g29.silent_manager.android_components.view_models.HomeViewModel
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import kotlinx.android.synthetic.main.content_loading_data.*
import kotlinx.android.synthetic.main.fragment_home.*
import android.content.Intent
import com.silent_manager.g29.silent_manager.android_components.activities.CreateEventActivity
import com.silent_manager.g29.silent_manager.android_components.activities.EventDetail
import com.silent_manager.g29.silent_manager.android_components.activities.EventDetail.Companion.EVENT_KEY_DATA
import com.silent_manager.g29.silent_manager.android_components.activities.LocationPermissionActivity


class HomeFragment : Fragment() {
    companion object {
        const val HOME_FRAGMENT_TAG = "Home_fragment"
    }

    private fun getViewModelFactory() = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return HomeViewModel(activity?.application as SilentManagerApplication) as T
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        context?.let {
            ShowScreenRules.run(HOME_FRAGMENT_TAG, it)
        }

        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onStart() {
        super.onStart()
        activateLoaderScreen(true)
        initViewModel()
    }

    private fun activateLoaderScreen(activate: Boolean) {
        if (activate) {
            loading_content_container.visibility = View.VISIBLE
            Home_Container.visibility = View.GONE
        } else {
            loading_content_container.visibility = View.GONE
            Home_Container.visibility = View.VISIBLE
        }
    }

    private fun initViewModel() {
        val viewModel: HomeViewModel = getViewModel()

        bindLocationData(viewModel)
        bindEventsData(viewModel)

        if (viewModel.getCurrentLocation().value == null)
            viewModel.updateLocation()
    }

    private fun bindLocationData(viewModel: HomeViewModel) {
        viewModel.getCurrentLocation().observe(this, Observer<Location?> { location ->
            location?.let {
                location_home_txt.text = it.address
            }
        })
    }

    private fun bindEventsData(viewModel: HomeViewModel) {
        viewModel.getCurrentEvents().observe(this, Observer<MutableList<Event>> { events ->
            if (events?.isEmpty() != null && !events.isEmpty()) {
                home_no_events_foud_txt.visibility = View.GONE
                addEventsToTheList(events)
            } else
                home_no_events_foud_txt.visibility = View.VISIBLE

            activateLoaderScreen(false)
        })
    }

    private fun getViewModel(): HomeViewModel {
        return ViewModelProviders
            .of(this, getViewModelFactory())
            .get(HomeViewModel::class.java)
    }

    private fun addEventsToTheList(events: MutableList<Event>) {
        val viewManager = LinearLayoutManager(activity)
        val viewAdapter = HomeEventAdapter(events, this::onEventClick)

        home_events_container.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun onEventClick(event: Event) {
        val intent = Intent(this.context, EventDetail::class.java)
        intent.putExtra(EVENT_KEY_DATA, event)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
