package com.silent_manager.g29.silent_manager.android_components.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.fragments.SearchFragment
import com.silent_manager.g29.silent_manager.android_components.helpers.HelperMethods
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import kotlinx.android.synthetic.main.content_event_detail.*
import java.text.SimpleDateFormat
import java.util.*

class EventDetail : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_detail)
        init()
    }

    companion object {
        const val EVENT_KEY_DATA = "kenaoevbr"
    }

    fun init() {

        if (intent != null && intent.extras != null && intent.extras.containsKey(EVENT_KEY_DATA)) {
            val event = intent.extras.getSerializable(EVENT_KEY_DATA) as? Event

            event_detail_title_value.text = event?.name
            event_detail_category_value.text = event?.category
            event_detail_starts_at_value.text = getDateFormatted(event?.startDate)
            event_detail_finish_at_value.text = getDateFormatted(event?.endDate)
            event_detail_latitude_value.text =
                HelperMethods.getFormattedDoubleToString(event?.location?.latitude)
            event_detail_longitudel_value.text =
                HelperMethods.getFormattedDoubleToString(event?.location?.longitude)
            event_detail_description_value.text = event?.description
            event_detail_map_img.setOnClickListener {
                onMapClick(event?.location, event?.radius)
            }
            event_detail_map_img.visibility = getMapVisibility()
            return
        }

        finish()
    }

    private fun getMapVisibility(): Int {
        val lm = LocationManager.getInstance()
        if (lm.isGoogleMapsInstalled(this)) {
            return View.VISIBLE
        }

        return View.GONE
    }

    private fun onMapClick(location: Location?, radius: Int?) {
        val intent = Intent(this, MapsActivity::class.java)
        intent.putExtra(MapsActivity.CHOSEN_LOCATION, location)
        intent.putExtra(MapsActivity.EVENT_FENCE, radius)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivityForResult(intent, MapsActivity.SEARCH_MAP_RESULT_CODE)
    }

    private fun getDateFormatted(date: Date?): String {
        if (date == null) return ""

        val datePattern = "yyyy/MM/dd HH:mm"
        return SimpleDateFormat(datePattern).format(date)
    }
}
