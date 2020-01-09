package com.silent_manager.g29.silent_manager.android_components.view_models

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.location.Address
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.helpers.Constants
import com.silent_manager.g29.silent_manager.android_components.helpers.Geocoder
import com.silent_manager.g29.silent_manager.android_components.helpers.runAsync
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import com.silent_manager.g29.silent_manager.data_layer.models.PageResult
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError

class FilteredEventsViewModel(applicationContext: SilentManagerApplication) :
    AndroidViewModel(applicationContext) {
    val events: MutableLiveData<MutableList<Event>> = MutableLiveData()

    fun updateFilteredEvents(locationName: String, category: String?) {
        runAsync {
            Geocoder.getAddressFromLocationName(
                locationName,
                getApplication<SilentManagerApplication>()
            )
        }.andThen {
            if (it != null && it.count() > 0) {
                val address = it[0]
                onConvertStringToAddress(address.latitude, address.longitude, category)
            } else {
                events.value = null
            }

        }

    }

    private fun onConvertStringToAddress(latitude: Double, longitude: Double, category: String?) {
        getApplication<SilentManagerApplication>().eventService.getEvents(
            latitude,
            longitude,
            Constants.DEFAULT_RADIUS_SIZE,
            category,
            ::onUpdateEventsSucceed,
            ::onFailedRequest
        )
    }

    private fun onFailedRequest(error: RequestError) {
        events.value = null
    }

    private fun onUpdateEventsSucceed(result: PageResult<Event>?) {
        result?.results?.let {
            events.value = it.toMutableList()
        }
    }
}