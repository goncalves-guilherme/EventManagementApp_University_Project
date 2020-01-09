package com.silent_manager.g29.silent_manager.android_components.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.managers.PermissionManager

import kotlinx.android.synthetic.main.content_location_permission.*

class LocationPermissionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_permission)

        enabe_location_permission_btm.setOnClickListener {
            askForLocationPermission()
        }
    }

    private fun askForLocationPermission() {
        val lm = com.silent_manager.g29.silent_manager.android_components.managers.LocationManager.getInstance()
        lm.requestLocationPermission(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PermissionManager.PermissionCodes.LOCATION_PERMISSION.ordinal -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    goToMainMenu()
                } else {
                    // User Rejects
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun goToMainMenu() {
        val intent = Intent(applicationContext, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(intent)
    }
}
