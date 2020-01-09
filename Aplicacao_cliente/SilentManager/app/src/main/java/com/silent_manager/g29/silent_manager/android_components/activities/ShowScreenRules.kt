package com.silent_manager.g29.silent_manager.android_components.activities

import android.content.Context
import android.support.v4.app.Fragment
import com.silent_manager.g29.silent_manager.android_components.fragments.HomeFragment
import com.silent_manager.g29.silent_manager.android_components.fragments.RequestLocationFragment
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager

object ShowScreenRules {
    fun run(viewTag: String, context: Context): Fragment? {
        return when (viewTag) {
            HomeFragment.HOME_FRAGMENT_TAG -> homeFragmentRules(context)
            else -> {
                null
            }
        }
    }

    private fun homeFragmentRules(context: Context): RequestLocationFragment? {
        val lm = LocationManager.getInstance()
        if (!lm.isLocationEnable(context)) {
            return RequestLocationFragment()
        }
        return null
    }
}