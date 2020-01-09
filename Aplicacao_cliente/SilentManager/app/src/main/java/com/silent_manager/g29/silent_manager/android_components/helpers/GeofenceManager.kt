package com.silent_manager.g29.silent_manager.android_components.helpers

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v4.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices
import com.silent_manager.g29.silent_manager.android_components.android_services.GeoFenceTriggerService
import com.silent_manager.g29.silent_manager.data_layer.models.Event

class GeofenceManager {
    private var pendingIntent: PendingIntent? = null

    private fun getPendingRequest(context: Context): PendingIntent {
        if (pendingIntent == null) {
            val intent = Intent(context, GeoFenceTriggerService::class.java)
            pendingIntent = PendingIntent.getService(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        return pendingIntent!!
    }

    fun stopGeofenceService(context: Context) {
        val geofencePendingIntent = getPendingRequest(context)
        val geofencingClient = LocationServices.getGeofencingClient(context)
        geofencingClient?.removeGeofences(geofencePendingIntent)?.run {
            addOnSuccessListener { }
            addOnFailureListener {  }
        }
    }

    fun startGeoFenceService(context: Context, events: Array<Event>) {

        if (events.isNotEmpty()) {
            val geofencePendingIntent = getPendingRequest(context)

            // Ask for permission if the user does not permit to access mobile's location.
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {

                val geofencingClient = LocationServices.getGeofencingClient(context)

                val geofenceObjects = arrayListOf<Geofence?>()

                events.forEach {
                    geofenceObjects.add(
                        buildGeofence(
                            it.eventId!!,
                            it.location?.latitude!!,
                            it.location?.longitude!!,
                            Geofence.NEVER_EXPIRE,//it.endDate?.time!! - it.startDate?.time!!,
                            Constants.DEFAULT_RADIUS_SIZE.toFloat()//it.radius?.toFloat()!!
                        )
                    )
                }

                geofencingClient.addGeofences(
                    buildGeofencingRequest(geofenceObjects), geofencePendingIntent
                )?.run {
                    addOnCompleteListener {}
                    addOnFailureListener {}
                }

            }
        }
    }

    private fun buildGeofence(
        eventId: Int,
        latitude: Double,
        longitude: Double,
        duration: Long,
        radius: Float
    ): Geofence? {
        return Geofence.Builder()
            // Set the request ID of the geofence. This is a string to identify this
            // geofence.
            .setRequestId(eventId.toString())

            // Set the circular region of this geofence.
            .setCircularRegion(
                latitude, // Latitude
                longitude, // Longitude
                radius// Radius
            )
            // Set the expiration duration of the geofence. This geofence gets automatically
            // removed after this period of time.
            .setExpirationDuration(duration)

            // Set the delay time to be triggered when the user gets inside the fence.
            //.setLoiteringDelay(100)

            // Set the transition types of interest. Alerts are only generated for these
            // transition. We track entry and exit transitions in this sample.
            .setTransitionTypes(
                Geofence.GEOFENCE_TRANSITION_ENTER
                        or Geofence.GEOFENCE_TRANSITION_EXIT
                //or Geofence.GEOFENCE_TRANSITION_DWELL
            )
            .build()
    }

    // Gives me the client request
    private fun buildGeofencingRequest(geofenceList: List<Geofence?>): GeofencingRequest {
        return GeofencingRequest.Builder().apply {
            // If user is already in the event's local before this service starts this line of code
            // will make sure that the GeoFence trigger will be called anyway.
            setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
            // Add the list of objects to listen in the map (Events)
            addGeofences(geofenceList)
        }.build()
    }
}