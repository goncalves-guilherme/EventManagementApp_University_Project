package com.silent_manager.g29.silent_manager.android_components.silence_modes

import android.content.Context
import android.util.Log
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.helpers.Constants
import com.silent_manager.g29.silent_manager.android_components.helpers.Geocoder
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.android_components.managers.MyAudioManager
import com.silent_manager.g29.silent_manager.android_components.managers.SilentFlowManager
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import com.silent_manager.g29.silent_manager.data_layer.models.PageResult
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import java.util.*


class GeofenceSilenceMode private constructor() : SilenceMode() {
    companion object {
        private val geofenceMode by lazy {
            GeofenceSilenceMode()
        }

        fun getInstance() = geofenceMode

        const val TAG = "GeofenceSilenceMode"
    }

    override fun run(context: Context) {
        // Async call to obtain last known location
        getLastKnownLocation(context) {
            if (it != null)
                runAutomaticallySilence(context, it)
        }
    }

    private fun isAudioOn(context: Context): Boolean {
        val am = MyAudioManager.getInstance()
        return am.isPhoneInAudioMode(context)
    }

    private fun isLastDownloadedEventsDateValid(context: Context): Boolean {
        val app = getApplicationContext(context)

        // Get the last time events where downloaded from the server.
        val lastDownloadedEventsDate =
            app.sharedPreferences.getStringData(SilentFlowManager.GEO_EVENTS_DOWNLOADED_DATE)
                .toFloatOrNull()

        if (lastDownloadedEventsDate != null) {
            val nowInSeconds = Calendar.getInstance()?.time?.time
            if (nowInSeconds != null)
                return lastDownloadedEventsDate + Constants.TIME_INTERVAL_TO_UPDATE_EVENTS_PER_MS < nowInSeconds
        }

        return false
    }

    private fun shouldDownloadNewEvents(context: Context, location: Location): Boolean {
        return isLocationNew(context, location) || isLastDownloadedEventsDateValid(context)
    }

    private fun runAutomaticallySilence(context: Context, location: Location) {
        val sfm = SilentFlowManager.getInstance()

        // If download new data and user is inside an event the phone will be in silent mode in the next iteration
        if (shouldDownloadNewEvents(context, location)) {
            sfm.invalidateData()
        }
        else {
            val geofenceEvents = sfm.getGeofencesList()

            removeTimeOutEvents(geofenceEvents)
            val events = getStartedEvents(geofenceEvents)

            val currEvent = sfm.currGeofence

            if (isAudioOn(context) /*&& !doesUserStillsOnTheSameEvent(currEvent, location)*/) {
                // Silence phone automatically
                val newEvent = getEventUserIsIn(events, location)
                if (newEvent != null) {
                    putInSilenceMode(context)
                    sfm.currGeofence = newEvent
                }

            } else if (didEventFinish(currEvent) || doesUserLeaveAnEvent(currEvent, location)) {
                backToNormalMode(context)
                sfm.currGeofence = null
            }

        }
    }

    private fun didEventFinish(currEvent: Event?): Boolean {
        val now = Calendar.getInstance()?.time

        if (currEvent?.endDate?.time != null && now?.time != null)
            return currEvent.endDate.time < now.time

        return false
    }

    private fun doesUserLeaveAnEvent(currEvent: Event?, location: Location): Boolean {
        currEvent?.let {
            return !isUserInsideEventCalculation(it, location)
        }

        return true
    }

    private fun isUserInsideEventCalculation(event: Event, location: Location): Boolean {
        if (event.radius != null && location.latitude != null && location.longitude != null
            && event.location?.latitude != null && event.location?.longitude != null
        ) {
            val results = floatArrayOf(0f)
            android.location.Location.distanceBetween(
                location.latitude,
                location.longitude,
                event.location.latitude,
                event.location.longitude,
                results
            )
            return results.isNotEmpty() && results[0] < event.radius
        }

        return false
    }

    private fun getEventUserIsIn(events: List<Event>, location: Location): Event? =
        events.firstOrNull {
            isUserInsideEventCalculation(it, location)
        }

    private fun getLastKnownLocation(context: Context, cb: (Location?) -> Unit) {
        LocationManager.getInstance().getLastLocation(context) {
            val address = Geocoder.getAddressFromLocation(it, context)
            cb(
                Location(
                    latitude = it?.latitude,
                    longitude = it?.longitude,
                    id = null,
                    address = address?.locality
                )
            )
        }
    }

    private fun getApplicationContext(context: Context): SilentManagerApplication {
        return context.applicationContext as SilentManagerApplication
    }

    private fun isLocationNew(context: Context, location: Location?): Boolean {
        if (location?.address == null) {
            return true
        }

        val app = getApplicationContext(context)
        val lastLocationRecorded =
            app.sharedPreferences.getStringData(SilentFlowManager.LAST_LOCATION_RECORDED)

        return lastLocationRecorded != location.address
    }
}