package com.silent_manager.g29.silent_manager.android_components.android_broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.silent_manager.g29.silent_manager.android_components.android_services.GeoFenceTriggerService


class GeofenceBootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        GeoFenceTriggerService.enqueueWork(
            context,
            intent
        )
    }



}
