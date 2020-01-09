package com.silent_manager.g29.silent_manager.android_components.android_services

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.support.v4.app.JobIntentService
import android.util.Log
import com.google.android.gms.location.*
import com.silent_manager.StaticRepo
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import java.util.*

class GeoFenceTriggerService : JobIntentService() {
    companion object {
        private val TAG = "GEOFENCETRIGGERSERVICE"
        private val JOB_ID = 573

        fun enqueueWork(context: Context, intent: Intent) {
            enqueueWork(context, GeoFenceTriggerService::class.java, JOB_ID, intent)
        }
    }

    override fun onHandleWork(intent: Intent) {
        onHandleIntent(intent)
    }

    private fun onHandleIntent(intent: Intent?) {
        // 1
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        // 2
        if (geofencingEvent.hasError()) {
            return stopSelf()
        }
        // 3
        handleEvent(geofencingEvent)
    }

    private fun handleEvent(event: GeofencingEvent) {

        if (event.geofenceTransition == Geofence.GEOFENCE_TRANSITION_ENTER) {
            if (doesAnyTriggeredEventStarted(event)) {
                setVibrateMode()
            }
        } else {
            setAudioMode()
        }
    }

    private fun doesAnyTriggeredEventStarted(event: GeofencingEvent): Boolean {
        return true
        /*
        return event.triggeringGeofences.any {
            val eventsRepo = getEventsAroundUser()
            return eventsRepo.find { event ->
                it.requestId.toInt() == event.eventId && Calendar.getInstance().time?.after(event.startDate)!!
            } != null
        }*/
    }

    private fun getEventsAroundUser(): ArrayList<Event> {
        return StaticRepo.getEvents()
    }

    private fun setVibrateMode() {
        try {
            val mode = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            mode?.ringerMode = AudioManager.RINGER_MODE_VIBRATE
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
    }

    private fun setAudioMode() {
        try {
            val mode = getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            mode?.ringerMode = AudioManager.RINGER_MODE_NORMAL
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
    }


}