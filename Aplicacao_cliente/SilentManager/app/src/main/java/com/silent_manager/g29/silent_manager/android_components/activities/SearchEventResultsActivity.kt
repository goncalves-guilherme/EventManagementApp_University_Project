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
import com.silent_manager.g29.silent_manager.android_components.adapters.HomeEventAdapter
import com.silent_manager.g29.silent_manager.android_components.view_models.FilteredEventsViewModel
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import kotlinx.android.synthetic.main.activity_search_event_results.*
import kotlinx.android.synthetic.main.content_loading_data.*
import kotlinx.android.synthetic.main.fragment_home.*

class SearchEventResultsActivity : AppCompatActivity() {
    private fun getViewModelFactory() = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return FilteredEventsViewModel(application as SilentManagerApplication) as T
        }
    }

    companion object {
        const val SEARCH_EVENT_LOCATION_FILTER = "SEARCH_EVENT_LOCATION_FILTER"
        const val SEARCH_EVENT_CATEGORY_FILTER = "SEARCH_EVENT_CATEGORY_FILTER"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_event_results)
        close_search_results_activity_btm.setOnClickListener { closeActivity() }
        activateLoaderScreen(true)
        initViewModel()
    }

    private fun closeActivity() {
        finish()
    }

    private fun activateLoaderScreen(activate: Boolean) {
        if (activate) {
            loading_content_container.visibility = View.VISIBLE
            search_events_containner.visibility = View.GONE
        } else {
            loading_content_container.visibility = View.GONE
            search_events_containner.visibility = View.VISIBLE
        }
    }

    private fun initViewModel() {

        val intent: Intent = intent
        val location = intent.getStringExtra(SEARCH_EVENT_LOCATION_FILTER)
        val category = intent.getStringExtra(SEARCH_EVENT_CATEGORY_FILTER)

        val viewModel: FilteredEventsViewModel = getViewModel()

        bindEventsData(viewModel)

        viewModel.updateFilteredEvents(location, category)
    }

    private fun bindEventsData(viewModel: FilteredEventsViewModel) {
        viewModel.events.observe(this, Observer<MutableList<Event>> { events ->
            if (events?.isEmpty() != null && !events.isEmpty()) {
                search_events_not_found.visibility = View.GONE
                addEventsToTheList(events)
            }
            else
                search_events_not_found.visibility = View.VISIBLE

            activateLoaderScreen(false)
        })
    }

    private fun addEventsToTheList(events: MutableList<Event>) {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = HomeEventAdapter(events, this::onEventClick)

        event_list_search_result.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun getViewModel(): FilteredEventsViewModel {
        return ViewModelProviders
            .of(this, getViewModelFactory())
            .get(FilteredEventsViewModel::class.java)
    }

    private fun onEventClick(event: Event) {
        val intent = Intent(this, EventDetail::class.java)
        intent.putExtra(EventDetail.EVENT_KEY_DATA, event)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}