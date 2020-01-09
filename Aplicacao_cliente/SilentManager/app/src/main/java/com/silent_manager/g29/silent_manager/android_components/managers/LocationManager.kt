package com.silent_manager.g29.silent_manager.android_components.managers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.support.v4.app.Fragment
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import com.google.android.gms.location.LocationServices
import android.util.Log
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import android.location.Location as AndroidLocation


class LocationManager private constructor() {
    companion object {
        const val ON_ENABLE_LOCATION_RESULT = 273
        private val locationManager by lazy { LocationManager() }
        fun getInstance() = locationManager
    }

    fun enableLocation(activity: Activity) {
        activity.startActivityForResult(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
            ON_ENABLE_LOCATION_RESULT
        )
    }

    fun enableLocation(fragment: Fragment) {
        fragment.startActivityForResult(
            Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
            ON_ENABLE_LOCATION_RESULT
        )
    }

    fun isGoogleMapsInstalled(context: Context?): Boolean {
        return try {
            val info =
                context?.packageManager?.getApplicationInfo("com.google.android.apps.maps", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }

    }

    fun isLocationEnable(context: Context): Boolean {
        val lm =
            context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        var gpsEnabled = false
        var networkEnabled = false

        try {
            gpsEnabled = lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
            networkEnabled = lm.isProviderEnabled(android.location.LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
            Log.d("LocationManager", ex.message)
        }

        return gpsEnabled && networkEnabled
    }

    private fun updateLocation(context: Context, callback: (AndroidLocation?) -> Unit) {
        val mLocationRequest = LocationRequest.create()

        mLocationRequest.interval = 60000
        mLocationRequest.fastestInterval = 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        LocationServices.getFusedLocationProviderClient(context)
                            .removeLocationUpdates(this)
                        callback(location)
                    }
                }
            }
        }
        LocationServices.getFusedLocationProviderClient(context)
            .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
    }

    @SuppressLint("MissingPermission")
    fun getLastLocation(context: Context, cb: (AndroidLocation?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (isLocationPermitted(context)) {
            fusedLocationClient.lastLocation.addOnSuccessListener {
                if (it == null) {
                    updateLocation(context, cb)
                } else {
                    cb(it)
                }
            }
        }
    }

    fun isLocationPermitted(context: Context): Boolean {
        val pm = PermissionManager.getInstance()
        return pm.doesUserAllowPermission(Manifest.permission.ACCESS_FINE_LOCATION, context)
    }

    fun requestLocationPermission(activity: Activity) {
        val pm = PermissionManager.getInstance()
        pm.requestUserPermission(
            PermissionManager.PermissionCodes.LOCATION_PERMISSION.ordinal,
            Manifest.permission.ACCESS_FINE_LOCATION, activity
        )
    }

    fun checkLocationStatus(context: Context): Boolean {
        val lm =
            context.getSystemService(Context.LOCATION_SERVICE) as android.location.LocationManager
        return lm.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)
    }
}