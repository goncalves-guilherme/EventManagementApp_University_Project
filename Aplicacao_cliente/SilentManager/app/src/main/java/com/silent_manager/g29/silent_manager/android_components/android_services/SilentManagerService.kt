package com.silent_manager.g29.silent_manager.android_components.android_services

import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.GcmTaskService
import com.google.android.gms.gcm.TaskParams
import com.silent_manager.g29.silent_manager.android_components.managers.SilentFlowManager

class SilentManagerService : GcmTaskService() {
    override fun onCreate() {
        super.onCreate()
        val i = 0
    }

    override fun onRunTask(p0: TaskParams?): Int {
        val sm = SilentFlowManager.getInstance()
        sm.silentFlow(this)
        return GcmNetworkManager.RESULT_SUCCESS
    }
}
