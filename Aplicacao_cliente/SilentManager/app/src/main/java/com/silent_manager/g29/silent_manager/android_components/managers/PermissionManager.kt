package com.silent_manager.g29.silent_manager.android_components.managers

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat

class PermissionManager private constructor() {
    companion object {
        private val permissionManager by lazy { PermissionManager() }
        fun getInstance() = permissionManager
    }

    enum class PermissionCodes {
        LOCATION_PERMISSION, READ_CONTACTS_PERMISSION, NOTIFICATION_POLICY_ACCESS_PERMISSION,
        OUT_GOING_CALLS_PERMISSION, READ_CALENDAR
    }

    private fun doesUserRequireRealTimeRequestPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    fun doesUserAllowPermission(permission: String, context: Context): Boolean {
        return !doesUserRequireRealTimeRequestPermission() ||
                ContextCompat.checkSelfPermission(context, permission) ==
                PackageManager.PERMISSION_GRANTED
    }

    fun requestUserPermission(permissionId: Int, permission: String, activityCaller: Activity) {

        if (doesUserRequireRealTimeRequestPermission()) {
            ActivityCompat.requestPermissions(
                activityCaller, arrayOf(permission), permissionId
            )
        }
    }

    fun requestUserPermission(permissionId: Int, permission: String, fragmentCaller: Fragment) {

        if (doesUserRequireRealTimeRequestPermission()) {
            fragmentCaller.requestPermissions(
                arrayOf(permission), permissionId
            )
        }
    }
}