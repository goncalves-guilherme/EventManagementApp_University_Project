package com.silent_manager.g29.silent_manager.android_components.android_broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.silent_manager.g29.silent_manager.android_components.managers.MyAudioManager
import com.silent_manager.g29.silent_manager.android_components.managers.SilentFlowManager

class RingerModeChangedReceiver : BroadcastReceiver() {

    /**
     * When user changed the ringer mode to silence manually the application will not bring
     * phone to audio mode if user leaves the event.
     * */
    override fun onReceive(context: Context?, intent: Intent?) {
        val sm = SilentFlowManager.getInstance()
        val am = MyAudioManager.getInstance()

        if (context != null && !am.wasAudioChangedStateSignedByThisApp(context)) {
            sm.setAutomaticSilenceByAppFlag(context, false)
        } else {
            am.resetAudioChangedStateSignedByThisApp(context)
        }
    }
}