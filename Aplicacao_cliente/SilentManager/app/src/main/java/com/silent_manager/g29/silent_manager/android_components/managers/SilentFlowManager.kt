package com.silent_manager.g29.silent_manager.android_components.managers

import android.content.Context
import android.util.Log
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.helpers.Constants
import com.silent_manager.g29.silent_manager.android_components.helpers.Geocoder
import com.silent_manager.g29.silent_manager.android_components.silence_modes.ScheduleSilenceMode
import com.silent_manager.g29.silent_manager.android_components.silence_modes.GeofenceSilenceMode
import com.silent_manager.g29.silent_manager.android_components.silence_modes.ISilenceMode
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.InviteState
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import java.util.*
import kotlin.collections.ArrayList


class SilentFlowManager private constructor() {
    companion object {
        private val silentManager by lazy {
            SilentFlowManager()
        }

        fun getInstance() = silentManager

        const val PHONE_SILENCE_BY_THIS_APP = "AudioAsInitialState"
        const val LAST_LOCATION_RECORDED = "LastLocationRecorded"
        const val GEO_EVENTS_DOWNLOADED_DATE = "GEO_EVENTS_DOWNLOADED_DATE"
        const val TAG = "GeofenceFLowManager"
    }

    private val _geofences: ArrayList<Event> = arrayListOf()
    private val _privateEvents: ArrayList<Event> = arrayListOf()
    var currGeofence: Event? = null
    // Check if this componenet were created for the first time.
    private var isDirty = true

    fun getPrivateEvents() = _privateEvents
    fun getGeofencesList() = _geofences

    fun invalidateData() {
        isDirty = true
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

    private fun downloadPublicEventsFromServer(context: Context, location: Location) {
        if (location.latitude != null && location.longitude != null) {
            val app = getApplicationContext(context)
            app.eventService.getEvents(
                location.latitude,
                location.longitude,
                Constants.DEFAULT_RADIUS_SIZE,
                null,
                { events -> onPublicEventsReceived(context, events?.results, location) },
                {})
        }
    }

    private fun downloadPublicEventsFromCache(context: Context, location: Location) {

    }

    private fun getPublicEvents(context: Context) {
        // Get all events around the user
        getLastKnownLocation(context) {
            it?.let { location ->
                val wm = WifiManager.getInstance()
                if (wm.isInternetEnable(context)) {
                    downloadPublicEventsFromServer(context, location)
                } else {
                    downloadPublicEventsFromCache(context, location)
                }
            }
        }
    }

    private fun onPublicEventsReceived(
        context: Context,
        events: Array<Event>?,
        location: Location
    ) {
        events?.let {
            _geofences.removeAll { true }
            _geofences.addAll(events)
            saveNewLocation(context, location)
        }
    }

    private fun saveNewLocation(context: Context, location: Location) {
        val app = getApplicationContext(context)

        location.address?.let {
            val nowInSeconds = Calendar.getInstance()?.time?.time
            app.sharedPreferences.putStringData(
                LAST_LOCATION_RECORDED,
                location.address
            )
            nowInSeconds?.let { downloadedDate ->
                app.sharedPreferences.putStringData(
                    GEO_EVENTS_DOWNLOADED_DATE,
                    downloadedDate.toString()
                )
            }
        }
    }

    private fun getPrivateEvents(context: Context) {
        val tm = TokenManager.getInstance()
        val token = tm.getToken(context)

        // Get all private events
        if (!token.AccessToken.isNullOrEmpty()) {
            getApplicationContext(context).invitationService.getInvites(
                token,
                null,
                InviteState.ACCEPTED_INVITE,
                { acceptedInvites ->
                    if (!acceptedInvites.isNullOrEmpty()) {
                        val privateEvents: List<Event> =
                            acceptedInvites
                                .map { it.event }
                                .filterNotNull()
                        _privateEvents.addAll(privateEvents)
                    }
                },
                { Log.d(TAG, it.message) })
        }
    }

    private fun getEventsToSilence(context: Context) {
        getPublicEvents(context)
        getPrivateEvents(context)
    }

    fun silentFlow(context: Context) {
        if (isDirty) {
            getEventsToSilence(context)
            isDirty = false
        }
        val mobileInAudioMode = isMobileInAudioMode(context)
        // Only enters in the flow if the phone is in audio mode or went to silence by this application
        if (mobileInAudioMode || wasMobileSetToSilenceByThisApp(context)
        ) {
            getSilentMode(context)
                .run(context)
        }
    }

    fun setAutomaticSilenceByAppFlag(context: Context, silence: Boolean) {
        val app = getApplicationContext(context)
        app.sharedPreferences.putBooleanData(PHONE_SILENCE_BY_THIS_APP, silence)
    }

    private fun getSilentMode(context: Context): ISilenceMode {
        if (isLocationActive(context) && isLocationPermitted(context))
            return GeofenceSilenceMode.getInstance()

        return ScheduleSilenceMode.getInstance()
    }

    private fun getApplicationContext(context: Context): SilentManagerApplication {
        return context.applicationContext as SilentManagerApplication
    }

    private fun isMobileInAudioMode(context: Context): Boolean {
        val am = MyAudioManager.getInstance()
        return am.isPhoneInAudioMode(context)
    }

    private fun wasMobileSetToSilenceByThisApp(context: Context): Boolean {
        val app = getApplicationContext(context)
        return app.sharedPreferences.getBooleanData(PHONE_SILENCE_BY_THIS_APP)
    }


    private fun isLocationPermitted(context: Context): Boolean {
        val lm = LocationManager.getInstance()
        return lm.isLocationPermitted(context)
    }

    private fun isLocationActive(context: Context): Boolean {
        val lm = LocationManager.getInstance()
        return lm.isLocationEnable(context)
    }

}
