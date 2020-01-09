package com.silent_manager.g29.silent_manager.android_components.silence_modes

import android.content.Context
import com.silent_manager.g29.silent_manager.android_components.managers.SilentFlowManager

class ScheduleSilenceMode private constructor(): SilenceMode(){
    companion object{
        private val scheduleMode by lazy {
            ScheduleSilenceMode()
        }

        fun getInstance() = scheduleMode
    }
    override fun run(context: Context) {
        val sfm = SilentFlowManager.getInstance()
        val privateEvents = sfm.getPrivateEvents()

        removeTimeOutEvents(privateEvents)

        val startedEvents = getStartedEvents(privateEvents)

        if (startedEvents.isNullOrEmpty()) {
            backToNormalMode(context)
        } else {
            putInSilenceMode(context)
        }
    }
}