package com.silent_manager.g29.silent_manager.android_components.android_broadcast

import android.app.Application
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.widget.Toast
import com.silent_manager.g29.silent_manager.android_components.managers.ContactsManager
import com.silent_manager.g29.silent_manager.android_components.managers.MyAudioManager


class PhoneCallReceiver : BroadcastReceiver() {
    val TAG: String = "PhoneCallReceiver"
    private var lastRingerMode: MyAudioManager.AudioMode = MyAudioManager.AudioMode.UNKNOWN

    override fun onReceive(context: Context, intent: Intent) {
        Toast.makeText(
            context, TAG,
            Toast.LENGTH_SHORT
        ).show()

        val am = MyAudioManager.getInstance()

        if (!am.isPhoneInAudioMode(context)) {
            lastRingerMode = am.getCurrentRingerMode(context)
            val numberExceptions: String = getExceptionNumbers(context)
            val nm = context.getSystemService(Application.NOTIFICATION_SERVICE) as NotificationManager
            val phoneCallListener =
                PhoneCallListener(
                    numberExceptions,
                    { am.changeAudioMode(context, it) },
                    lastRingerMode
                )

            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL)
            }
            tm.listen(phoneCallListener, PhoneStateListener.LISTEN_CALL_STATE)
        }
    }

    private fun getExceptionNumbers(context: Context): String {
        val cm = ContactsManager.getInstance()
        return cm.getContactExceptionsString(context)
    }
}
