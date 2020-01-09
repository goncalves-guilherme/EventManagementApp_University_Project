package com.silent_manager.g29.silent_manager.android_components.view_models

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.location.Address
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.helpers.Constants
import com.silent_manager.g29.silent_manager.android_components.helpers.Geocoder
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.android_components.helpers.runAsync
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import com.silent_manager.g29.silent_manager.data_layer.models.PageResult
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError


class HomeViewModel(applicationContext: SilentManagerApplication) : AndroidViewModel(applicationContext) {
    private val _currentLocation: MutableLiveData<Location> = MutableLiveData()
    private val _events: MutableLiveData<MutableList<Event>> = MutableLiveData()

    // The responsability to set values on mutable live data it belongs to View Models only
    fun getCurrentLocation(): LiveData<Location> = _currentLocation

    fun getCurrentEvents(): LiveData<MutableList<Event>> = _events


    fun updateEvents(latitude: Double, longitude: Double, radius: Int) {
        getApplication<SilentManagerApplication>().eventService.getEvents(
            latitude,
            longitude,
            radius,
            null,
            ::onUpdateEventsSucceed,
            ::onFailedRequest
        )
    }

    private fun onFailedRequest(error: RequestError) {
        _events.value = null
    }

    private fun onUpdateEventsSucceed(result: PageResult<Event>?) {
        result?.results?.let {
            _events.value = it.toMutableList()
        }
    }

    fun updateLocation() {
        getLastLocation { location ->
            runAsync {
                Geocoder.getAddressFromLocation(location, getApplication<SilentManagerApplication>())
            }.andThen {
                onAddressReceive(it)
                location?.let { loc ->
                    updateEvents(loc.latitude, loc.longitude, Constants.DEFAULT_RADIUS_SIZE)
                }
            }
        }
    }

    private fun onAddressReceive(address: Address?) {
        address?.let {
            _currentLocation.value = Location(
                id = null,
                latitude = address.latitude,
                longitude = address.longitude,
                address = if (address.locality.isNullOrBlank()) address.countryName else address.locality
            )
        }
    }

    private fun getLastLocation(cb: (android.location.Location?) -> Unit) {
        LocationManager.getInstance().getLastLocation(getApplication<SilentManagerApplication>(), cb)
    }
}