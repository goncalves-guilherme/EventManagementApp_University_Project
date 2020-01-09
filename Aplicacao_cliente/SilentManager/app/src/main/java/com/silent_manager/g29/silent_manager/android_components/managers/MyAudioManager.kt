package com.silent_manager.g29.silent_manager.android_components.managers

import android.Manifest
import android.content.Context
import android.media.AudioManager
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.util.Log
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication.Companion.AUDIO_MODE_CHANGED_BY_THIS_APP
import android.content.Intent
import android.app.NotificationManager
import android.os.Build




class MyAudioManager private constructor() {
    companion object {
        private val audioManager by lazy { MyAudioManager() }
        fun getInstance() = audioManager
        const val TAG: String = "MYAudioManager"
    }

    enum class AudioMode {
        NO_AUDIO, VIBRATE, AUDIO, UNKNOWN
    }

    fun isNotificationPolicyAccessPermitted(context: Context): Boolean {

        val n = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            n.isNotificationPolicyAccessGranted
        } else {
            true
        }
    }

    fun requestNotificationPolicyAccessPermitted(fragment: Fragment) {
        val intent = Intent(android.provider.Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
        //fragment.startActivityForResult(intent, PermissionManager.PermissionCodes.NOTIFICATION_POLICY_ACCESS_PERMISSION.ordinal)
        fragment.activity?.startActivityForResult(intent, PermissionManager.PermissionCodes.NOTIFICATION_POLICY_ACCESS_PERMISSION.ordinal)
    }

    fun isPhoneInAudioMode(context: Context): Boolean {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager
        return audio.ringerMode == android.media.AudioManager.RINGER_MODE_NORMAL
    }

    fun resetAudioChangedStateSignedByThisApp(context: Context?) {
        val app = context?.applicationContext as SilentManagerApplication
        app.sharedPreferences.putBooleanData(AUDIO_MODE_CHANGED_BY_THIS_APP, false)
    }

    fun wasAudioChangedStateSignedByThisApp(context: Context?): Boolean {
        val app = context?.applicationContext as SilentManagerApplication
        return app.sharedPreferences.getBooleanData(AUDIO_MODE_CHANGED_BY_THIS_APP)
    }

    fun changeAudioMode(context: Context, mode: AudioMode) {
        val app = context.applicationContext as SilentManagerApplication
        app.sharedPreferences.putBooleanData(AUDIO_MODE_CHANGED_BY_THIS_APP, true)

        when (mode) {
            AudioMode.NO_AUDIO -> setNoSoundMode(context)
            AudioMode.VIBRATE -> setVibrateMode(context)
            AudioMode.AUDIO -> setAudioMode(context)
        }
    }

    fun getCurrentRingerMode(context: Context): AudioMode {
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as android.media.AudioManager

        return when (audio.ringerMode) {
            AudioManager.RINGER_MODE_VIBRATE -> AudioMode.VIBRATE
            AudioManager.RINGER_MODE_NORMAL -> AudioMode.AUDIO
            AudioManager.RINGER_MODE_SILENT -> AudioMode.NO_AUDIO
            else -> AudioMode.UNKNOWN
        }
    }

    private fun setNoSoundMode(context: Context) {
        try {
            val mode = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            mode?.ringerMode = AudioManager.RINGER_MODE_SILENT
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
    }

    private fun setVibrateMode(context: Context) {
        try {
            val mode = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            mode?.ringerMode = AudioManager.RINGER_MODE_VIBRATE
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
    }

    private fun setAudioMode(context: Context) {
        try {
            val mode = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager?
            mode?.ringerMode = AudioManager.RINGER_MODE_NORMAL
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
    }
}