package com.silent_manager.g29.silent_manager.android_components.android_broadcast

import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import com.silent_manager.g29.silent_manager.android_components.managers.MyAudioManager

class PhoneCallListener(
    private val numberExceptions: String,
    private val setAudioMode: (MyAudioManager.AudioMode) -> Unit,
    private val lastRingerMode: MyAudioManager.AudioMode
) :
    PhoneStateListener() {
    var LOG_TAG = "PhoneListener"

    override fun onCallStateChanged(state: Int, incomingNumber: String) {
        if (numberExceptions.contains(incomingNumber)) {
            when (state) {
                TelephonyManager.CALL_STATE_RINGING -> {
                    Log.i(LOG_TAG, "RINGING")
                    setAudioMode(MyAudioManager.AudioMode.AUDIO)
                }
                TelephonyManager.CALL_STATE_OFFHOOK -> {
                    Log.i(LOG_TAG, "OFFHOOK")
                    setAudioMode(lastRingerMode)
                }
                TelephonyManager.CALL_STATE_IDLE -> {
                    Log.i(LOG_TAG, "IDLE")
                    setAudioMode(lastRingerMode)
                }
            }
        }
    }
}