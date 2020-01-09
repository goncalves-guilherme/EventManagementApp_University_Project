package com.silent_manager.g29.silent_manager.android_components.managers

import android.content.Context
import android.net.ConnectivityManager
import android.os.Build

class WifiManager private constructor() {
    companion object {
        private val wifiManager by lazy { WifiManager() }
        fun getInstance() = wifiManager
        const val TAG: String = "wifiManager"
    }


    fun isInternetEnable(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.activeNetwork != null
        } else {
            val wifi = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as android.net.wifi.WifiManager
            return wifi.isWifiEnabled
        }
    }
}