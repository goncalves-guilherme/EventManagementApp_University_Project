package com.silent_manager.g29.silent_manager.android_components.silence_modes

import android.content.Context
import com.silent_manager.g29.silent_manager.android_components.managers.MyAudioManager
import com.silent_manager.g29.silent_manager.android_components.managers.SilentFlowManager
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import java.util.*

abstract class SilenceMode : ISilenceMode {
    protected fun putInSilenceMode(context: Context) {
        val sfm = SilentFlowManager.getInstance()
        // Flag used to make phone back to normal mode only if was silence by this app.
        sfm.setAutomaticSilenceByAppFlag(context, true)

        val am = MyAudioManager.getInstance()
        am.changeAudioMode(context, MyAudioManager.AudioMode.VIBRATE)
    }

    protected fun backToNormalMode(context: Context) {
        val sfm = SilentFlowManager.getInstance()
        // Flag used to make phone back to normal mode only if was silence by this app.
        sfm.setAutomaticSilenceByAppFlag(context, false)

        val am = MyAudioManager.getInstance()
        am.changeAudioMode(context, MyAudioManager.AudioMode.AUDIO)
    }

    protected fun removeTimeOutEvents(events: ArrayList<Event>) {
        val now = Calendar.getInstance()?.time

        events.removeAll { e ->
            var toBeRemoved = true
            if (e.endDate != null)
                now?.time?.let {
                    toBeRemoved = e.endDate.time < now.time
                }

            toBeRemoved
        }
    }

    protected fun getStartedEvents(events: ArrayList<Event>): List<Event> {
        val now = Calendar.getInstance()?.time

        return events.filter { e ->
            var ret = false
            if (e.startDate != null && now?.time != null)
                ret = e.startDate <= now

            ret
        }
    }
}