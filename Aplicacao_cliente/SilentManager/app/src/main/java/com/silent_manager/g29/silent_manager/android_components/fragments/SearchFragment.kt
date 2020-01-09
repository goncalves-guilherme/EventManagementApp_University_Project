package com.silent_manager.g29.silent_manager.android_components.fragments

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import android.widget.ArrayAdapter
import android.widget.Spinner
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.activities.MapsActivity
import com.silent_manager.g29.silent_manager.android_components.activities.SearchEventResultsActivity
import com.silent_manager.g29.silent_manager.android_components.helpers.Geocoder
import com.silent_manager.g29.silent_manager.android_components.input_regex_filters.FilterInputTypedData
import kotlinx.android.synthetic.main.fragment_search.*
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.data_layer.models.Location


class SearchFragment : Fragment() {
    companion object {
        const val SEARCH_FRAGMENT_TAG = "Search_fragment"

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onStart() {
        super.onStart()
        initActivity()
    }

    private fun getMapVisibility(): Int {
        val lm = LocationManager.getInstance()
        return if (lm.isGoogleMapsInstalled(this.context))
            View.VISIBLE
        else
            View.GONE
    }

    private fun initActivity() {
        initAutoCompleteLocation()
        initSpinnerEventCategories()
        search_btm.setOnClickListener { onSearchClick() }
        search_fragment_map_img.setOnClickListener { openMap() }
        search_fragment_map_img.visibility = getMapVisibility()
        addInputFilter()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MapsActivity.SEARCH_MAP_RESULT_CODE -> {
                val location = data?.getSerializableExtra(MapsActivity.CHOSEN_LOCATION) as? Location
                context?.let {
                    if (location?.longitude != null && location.latitude != null) {
                        Geocoder.getAddressFromLocation(
                            location.latitude,
                            location.longitude,
                            it
                        ) { address ->
                            address?.let {
                                location_filter_value.setText(address.locality)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun openMap() {
        val intent = Intent(this.context, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivityForResult(intent, MapsActivity.SEARCH_MAP_RESULT_CODE)
    }

    private fun addInputFilter() {
        location_filter_value.filters =
            arrayOf(
                FilterInputTypedData(
                    FilterInputTypedData.LOCATION_TYPE_FILTER_REGEX
                ),
                InputFilter.LengthFilter(resources.getInteger(R.integer.small_input_max_size))
            )
    }

    private fun onSearchClick() {

        val filteredLocation: String = location_filter_value.text.toString()
        var filteredCategory: String? = create_event_category_spinner.selectedItem.toString()

        // Make the search only if the location is filled with data.
        if (!filteredLocation.isNullOrBlank()) {
            if (filteredCategory.equals("Any"))
                filteredCategory = ""

            goToFilteredEventsListView(filteredLocation, filteredCategory)
        }
    }

    private fun goToFilteredEventsListView(filteredLocation: String, filteredCategory: String?) {

        val intent = Intent(context?.applicationContext, SearchEventResultsActivity::class.java)

        intent.putExtra(SearchEventResultsActivity.SEARCH_EVENT_LOCATION_FILTER, filteredLocation)
        intent.putExtra(SearchEventResultsActivity.SEARCH_EVENT_CATEGORY_FILTER, filteredCategory)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP

        startActivity(intent)
    }

    private fun initSpinnerEventCategories() {
        val spinner: Spinner = create_event_category_spinner

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            context,
            R.array.event_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun initAutoCompleteLocation() {
        val locations = mutableListOf<String>()
        val locationAutoComplete = location_filter_value
        val adapter: ArrayAdapter<String> =
            ArrayAdapter(context, android.R.layout.simple_list_item_1, locations)

        locationAutoComplete.setAdapter(adapter)
        //locationAutoComplete.addTextChangedListener(getOnTextChangeListener(locations))
    }

    private fun getOnTextChangeListener(locations: MutableList<String>): TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (count == 3) {
                    val suggestivelocations = Geocoder
                        .getAddressFromLocationName(s.toString(), context?.applicationContext!!)
                        ?.map {
                            it.countryName
                        }

                    locations.removeAll { true }
                    suggestivelocations?.let {
                        locations.addAll(suggestivelocations.asIterable())
                    }

                }
            }

        }
    }
}
