package com.silent_manager.g29.silent_manager.android_components.fragments


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.activities.MainActivity
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import kotlinx.android.synthetic.main.fragment_request_location.*

class RequestLocationFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_location, container, false)
    }

    override fun onStart() {
        super.onStart()

        request_location_enable_btm.setOnClickListener {
            enableLocation()
        }
    }

    private fun enableLocation() {
        val lm = com.silent_manager.g29.silent_manager.android_components.managers.LocationManager.getInstance()
        lm.enableLocation(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            LocationManager.ON_ENABLE_LOCATION_RESULT -> {
                val lm = LocationManager.getInstance()
                context?.let {
                    if(lm.isLocationEnable(it)){
                        val i = Intent(it.applicationContext, MainActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(i)
                    }
                }
            }
        }
    }
}
