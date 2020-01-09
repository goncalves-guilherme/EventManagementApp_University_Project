package com.silent_manager.g29.silent_manager.android_components.android_services

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.content.Context
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams
import com.google.android.gms.location.LocationServices
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.helpers.Constants
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.PageResult
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import com.silent_manager.StaticRepo
import android.app.NotificationManager
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.helpers.GeofenceManager


class UpdateGeofenceIntentService : GcmTaskService() {

    override fun onRunTask(p0: TaskParams?): Int {
        updateEvents()
        return GcmNetworkManager.RESULT_SUCCESS
    }

    private fun updateEvents() {
        getLastLocation { location ->
            (application as SilentManagerApplication).eventService.getEvents(
                location.latitude,
                location.altitude,
                Constants.DEFAULT_RADIUS_SIZE,
                null,
                ::onUpdatedEvents,
                ::onUpdatedErrorEvent
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation(cb: (android.location.Location) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val lm = com.silent_manager.g29.silent_manager.android_components.managers.LocationManager.getInstance()
        if (lm.isLocationPermitted(this)) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: android.location.Location? ->
                    location?.let {
                        cb(location)
                    }
                }
        }
    }

    private fun onUpdatedErrorEvent(error: RequestError) {}

    private fun onUpdatedEvents(events: PageResult<Event>?) {
        events?.let {
            restartGeofenceServices(it)
        }
    }

    private fun restartGeofenceServices(events: PageResult<Event>) {
        events.results?.let {
            triggerNotification()
            GeofenceManager()
                .startGeoFenceService(applicationContext, StaticRepo.getEvents().toList().toTypedArray())
        }
    }

    companion object {
        private const val CHANNEL_ID: String = "Update_Events_And_Restarting_Geofence_Service"
        private const val NOTIFICATION_ID: Int = 123
    }

    private fun triggerNotification() {
        val builder = getNotification()
        createNotificationChannel()
        showNotification(builder)
    }

    private fun showNotification(builder: NotificationCompat.Builder) {
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(NOTIFICATION_ID, builder.build())
        }
    }

    private fun getNotification(): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.navigation_empty_icon)
            .setContentTitle(getString(R.string.Update_Events_Notification_Title))
            .setContentText(getString(R.string.Update_Events_Notification_Description))
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)!!
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.Update_Events_Notification_Title)
            val descriptionText = getString(R.string.Update_Events_Notification_Description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
