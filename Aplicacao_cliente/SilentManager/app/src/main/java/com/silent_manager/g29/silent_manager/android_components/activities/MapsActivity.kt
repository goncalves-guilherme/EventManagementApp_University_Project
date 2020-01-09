package com.silent_manager.g29.silent_manager.android_components.activities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.data_layer.models.Location
import com.google.android.gms.maps.model.CircleOptions
import android.graphics.Color


class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
    companion object {
        const val SEARCH_MAP_RESULT_CODE = 69
        const val CHOSEN_LOCATION = "locationchoose"
        const val EVENT_FENCE = "RADIUS"
    }

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun onClickMap(latitude: Double, longitude: Double) {
        val location =
            Location(latitude = latitude, longitude = longitude, address = null, id = null)

        val intent = Intent()
        intent.putExtra(CHOSEN_LOCATION, location)
        setResult(SEARCH_MAP_RESULT_CODE, intent)

        finish()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        val defaultLocation = intent.getSerializableExtra(CHOSEN_LOCATION) as? Location
        val radiusAux = intent.getIntExtra(EVENT_FENCE, 0)

        if (defaultLocation != null) {
            initializeMap(googleMap, defaultLocation.latitude, defaultLocation.longitude, radiusAux)
            return
        }

        val lm = LocationManager.getInstance()
        lm.getLastLocation(this) { l ->
            initializeMap(googleMap, l?.latitude, l?.longitude, radiusAux)
        }
    }

    private fun initializeMap(
        googleMap: GoogleMap,
        latitude: Double?,
        longitude: Double?,
        radius: Int
    ) {
        mMap = googleMap

        if (latitude != null && longitude != null) {
            // Add a marker in Sydney and move the camera
            val currLocation = LatLng(latitude, longitude)
            mMap.addMarker(MarkerOptions().position(currLocation).title("You are here."))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currLocation, 17f))
            radius?.let {
                mMap.addCircle(
                    CircleOptions()
                        .center(LatLng(latitude, longitude))
                        .radius(radius.toDouble())
                        .fillColor(Color.TRANSPARENT)
                )
            }
        }
        mMap.setOnMapClickListener {
            onClickMap(it.latitude, it.longitude)
        }
    }
}
