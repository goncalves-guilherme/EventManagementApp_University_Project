package com.silent_manager.g29.silent_manager.android_components.activities

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.*
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.input_regex_filters.FilterInputTypedData
import com.silent_manager.g29.silent_manager.android_components.view_models.CreateEventViewModel
import android.arch.lifecycle.Observer
import android.view.View
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import kotlinx.android.synthetic.main.activity_create_event.*
import kotlinx.android.synthetic.main.content_loading_data.*
import java.text.SimpleDateFormat
import java.util.*
import android.app.Activity
import android.content.Intent
import android.text.InputFilter
import com.silent_manager.g29.silent_manager.android_components.helpers.Geocoder
import com.silent_manager.g29.silent_manager.android_components.helpers.HelperMethods
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import kotlinx.android.synthetic.main.activity_create_event.create_event_category_spinner
import kotlinx.android.synthetic.main.fragment_search.*


class CreateEventActivity : AppCompatActivity() {
    companion object {
        const val EVENT_TO_UPDATE_KEY = "EVENTOO_UPDATE"
    }

    private fun getViewModelFactory() = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return CreateEventViewModel(application as SilentManagerApplication) as T
        }
    }

    private fun getMapVisibility(): Int {
        val lm = LocationManager.getInstance()
        return if (lm.isGoogleMapsInstalled(this))
            View.VISIBLE
        else
            View.GONE
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_event)

        init()
    }

    private fun setUpdateMode(event: Event?) {
        create_event_name_value.setText(event?.name)
        create_event_category_spinner.setSelection(
            (create_event_category_spinner.adapter as ArrayAdapter<String>).getPosition(
                event?.category
            )
        )
        create_event_latitude_field.setText(event?.location?.latitude.toString())
        create_event_longitude_field.setText(event?.location?.longitude.toString())
        create_event_radius_field.setText(event?.radius.toString())
        create_event_description_value.setText(event?.description)
        create_event_title_txt.setText(R.string.update_event_title)
        create_event_btm.setText(R.string.save_event_button_label)
        create_event_start_date_value.text = HelperMethods.getDateFormatted(event?.startDate)
        create_event_end_date_value.text = HelperMethods.getDateFormatted(event?.endDate)
        create_event_id.text = event?.eventId.toString()
    }


    private fun activateLoaderScreen(activate: Boolean) {
        enableViews(!activate)
        if (activate) {
            loading_content_container?.visibility = View.VISIBLE
        } else {
            loading_content_container?.visibility = View.GONE
        }
    }

    private fun init() {
        activateLoaderScreen(false)

        initSpinnerEventCategories()
        // Set view to update mode
        if (intent.extras != null && intent.extras.containsKey(EVENT_TO_UPDATE_KEY)) {
            setUpdateMode(intent.extras.getSerializable(EVENT_TO_UPDATE_KEY) as? Event)
        }

        create_event_btm.setOnClickListener { createEvent() }

        create_event_start_date_value.setOnClickListener {
            onDefineDateClick(it as TextView)
        }

        create_event_end_date_value.setOnClickListener {
            onDefineDateClick(it as TextView)
        }

        create_event_curr_location_img.setOnClickListener {
            val lm = LocationManager.getInstance()
            lm.getLastLocation(this) { location ->
                location?.let {
                    create_event_latitude_field.setText(location.latitude.toString())
                    create_event_longitude_field.setText(location.longitude.toString())
                }
            }
        }

        create_event_map_img.setOnClickListener { openMap() }
        create_event_map_img.visibility = getMapVisibility()

        addInputFilters()

        getViewModel().createdEvent().observe(this, Observer {
            if (it != null) {
                Toast
                    .makeText(this, R.string.create_event_success_to_create, Toast.LENGTH_SHORT)
                    .show()

                if (intent.extras != null && intent.extras.containsKey(EVENT_TO_UPDATE_KEY)) {
                    val returnIntent = Intent()
                    returnIntent.putExtra(EVENT_TO_UPDATE_KEY, it)
                    setResult(Activity.RESULT_OK, returnIntent)
                    finish()
                }

                finish()
            } else {
                Toast
                    .makeText(this, R.string.create_event_failed_to_create, Toast.LENGTH_SHORT)
                    .show()
            }
            activateLoaderScreen(false)
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            MapsActivity.SEARCH_MAP_RESULT_CODE -> {
                val location = data?.getSerializableExtra(MapsActivity.CHOSEN_LOCATION) as? Location
                if (location?.latitude != null && location.longitude != null) {
                    create_event_latitude_field.setText(location.latitude.toString())
                    create_event_longitude_field.setText(location.longitude.toString())
                }
            }
        }
    }

    private fun openMap() {
        val intent = Intent(this, MapsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivityForResult(intent, MapsActivity.SEARCH_MAP_RESULT_CODE)
    }

    private fun enableViews(enable: Boolean) {
        create_event_name_value.isEnabled = enable
        create_event_category_spinner.isEnabled = enable
        create_event_latitude_field.isEnabled = enable
        create_event_longitude_field.isEnabled = enable
        create_event_radius_field.isEnabled = enable
        create_event_description_value.isEnabled = enable
        create_event_btm.isEnabled = enable
        create_event_start_date_value.isEnabled = enable
        create_event_end_date_value.isEnabled = enable
    }

    private fun addInputFilters() {
        create_event_name_value.filters =
            arrayOf(
                FilterInputTypedData(FilterInputTypedData.BASIC_NAME_TYPE_FILTER_REGEX),
                InputFilter.LengthFilter(resources.getInteger(R.integer.small_input_max_size))
            )

        create_event_description_value.filters =
            arrayOf(
                FilterInputTypedData(FilterInputTypedData.BASIC_NAME_TYPE_FILTER_REGEX),
                InputFilter.LengthFilter(resources.getInteger(R.integer.small_input_max_size))
            )

        create_event_latitude_field.filters =
            arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.location_max_input)))

        create_event_longitude_field.filters =
            arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.location_max_input)))
        
        create_event_radius_field.filters =
            arrayOf(InputFilter.LengthFilter(resources.getInteger(R.integer.radius_max_input)))
    }

    private fun initSpinnerEventCategories() {
        val spinner: Spinner = create_event_category_spinner

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.event_categories,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    private fun onDefineDateClick(view: TextView) {
        val calendar: Calendar = Calendar.getInstance()

        DatePickerDialog(
            this,
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                val chosenTime: Calendar = Calendar.getInstance()
                chosenTime.set(year, monthOfYear, dayOfMonth)
                view.text = getDateFormatted(year, monthOfYear, dayOfMonth, 0, 0)
                displayTimePicker(view, year, monthOfYear, dayOfMonth)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun displayTimePicker(view: TextView, year: Int, month: Int, day: Int) {
        TimePickerDialog(
            this, TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
                view.text = getDateFormatted(year, month, day, hourOfDay, minute)
            },
            0,
            0,
            true
        ).show()
    }

    private fun getDateFormatted(years: Int, month: Int, day: Int, hour: Int, minute: Int): String {
        return "$years/$month/$day $hour:$minute"
    }

    private fun getDate(dateString: String): Date? {
        val dateTime = dateString.split(" ")

        if (!dateTime.isEmpty()) {
            val date = dateTime[0].split("/")
            val time = dateTime[1].split(":")

            if (date.size == 3 && time.size == 2) {
                val year = date[0].toIntOrNull()
                val month = date[1].toIntOrNull()
                val day = date[2].toIntOrNull()

                val hour = time[0].toIntOrNull()
                val minute = time[1].toIntOrNull()

                if (year != null && month != null && day != null && hour != null && minute != null) {
                    val c = Calendar.getInstance()
                    c.set(year, month, day, hour, minute)
                    return Date(c.timeInMillis)
                }
            }
        }

        return null
    }

    private fun createEvent() {

        val latitude = create_event_latitude_field.text.toString().toDoubleOrNull()
        val longitude = create_event_longitude_field.text.toString().toDoubleOrNull()
        val radius = create_event_radius_field.text.toString().toIntOrNull()
        val startDate = getDate(create_event_start_date_value.text.toString())
        val endDate = getDate(create_event_end_date_value.text.toString())
        val category = create_event_category_spinner.selectedItem.toString()
        val eventId = create_event_id.text.toString().toIntOrNull()
        val eventName = create_event_name_value.text.toString()
        val description = create_event_description_value.text.toString()

        if (latitude == null || longitude == null || radius == null || startDate == null || endDate == null) {
            return Toast.makeText(
                this,
                R.string.create_event_failed_missing_fields,
                Toast.LENGTH_SHORT
            ).show()
        }

        // Activate the loader screen
        activateLoaderScreen(true)

        // Create the event
        getViewModel().createEvent(
            eventId = eventId,
            name = eventName,
            desc = description,
            latitude = latitude,
            longitude = longitude,
            radius = radius,
            startDate = startDate,
            endDate = endDate,
            category = category
        )
    }

    private fun getViewModel(): CreateEventViewModel {
        return ViewModelProviders
            .of(this, getViewModelFactory())
            .get(CreateEventViewModel::class.java)
    }

}
