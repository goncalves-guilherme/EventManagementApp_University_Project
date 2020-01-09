package com.silent_manager.g29.silent_manager.android_components.helpers

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.util.Log
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication

class Geocoder {
    companion object {
        private const val GEOCODER_TAG: String = "Geocoder"
        fun getAddressFromLocation(
            location: android.location.Location?,
            context: Context
        ): Address? {
            val gcd = Geocoder(context)

            location?.let {

                var addresses: List<Address>? = null
                try {
                    addresses = gcd.getFromLocation(location.latitude, location.longitude, 1)
                } catch (e: Exception) {
                    Log.d(GEOCODER_TAG, e.message)
                }
                if (!addresses.isNullOrEmpty())
                    return addresses[0]
            }
            return null
        }

        fun getAddressFromLocation(
            latitude: Double,
            longitude: Double,
            context: Context,
            onAddressReceived: (Address?) -> Unit
        ) {
            runAsync {
                getAddressFromLocation(latitude, longitude, context)
            }.andThen {
                onAddressReceived(it)
            }
        }

        fun getAddressFromLocation(
            latitude: Double,
            longitude: Double,
            context: Context
        ): Address? {
            val gcd = Geocoder(context)
            var addresses: List<Address>? = null
            try {
                addresses = gcd.getFromLocation(latitude, longitude, 1)
            } catch (e: Exception) {
                Log.d(GEOCODER_TAG, e.message)
            }
            if (!addresses.isNullOrEmpty())
                return addresses[0]

            return null

        }

        fun getAddressFromLocationName(
            locationName: String,
            context: Context
        ): MutableList<Address>? {
            val gcd = Geocoder(context)

            locationName?.let {
                val addresses = gcd.getFromLocationName(locationName, 100)
                if (addresses.size > 0)
                    return addresses

                return null
            }
        }
    }
}