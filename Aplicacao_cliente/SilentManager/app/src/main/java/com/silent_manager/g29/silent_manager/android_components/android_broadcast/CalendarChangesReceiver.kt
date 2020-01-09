package com.silent_manager.g29.silent_manager.android_components.android_broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.util.Log

class CalendarChangesReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context?, intent: Intent?) {
        val i = 0
        CalendarContract.CONTENT_URI
        Log.d("adsfa", "sdafsdf")
    }
}