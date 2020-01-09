package com.silent_manager.g29.silent_manager.android_components

import android.app.Application
import android.content.Context
import com.android.volley.toolbox.Volley
import com.silent_manager.g29.silent_manager.data_layer.repository.EventRepository
import com.silent_manager.g29.silent_manager.data_layer.repository.UserRepository
import com.silent_manager.g29.silent_manager.data_layer.request.HttpRequest
import com.google.android.gms.gcm.GcmNetworkManager
import com.google.android.gms.gcm.PeriodicTask
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.android_services.SilentManagerService
import com.silent_manager.g29.silent_manager.android_components.helpers.*
import com.silent_manager.g29.silent_manager.android_components.managers.LocationManager
import com.silent_manager.g29.silent_manager.business_layer.services.*
import com.silent_manager.g29.silent_manager.data_layer.repository.AccountRepository
import com.silent_manager.g29.silent_manager.data_layer.repository.InviteRepository
import com.silent_manager.g29.silent_manager.data_layer.request.ErrorInterpreter


class SilentManagerApplication : Application() {
    companion object {
        const val SILENT_MANAGER: String = "SilentFlowManager"
        const val SILENT_MANAGER_SERVICE: String = "SilentManagerService"
        const val APP_ALREADY_OPENED_PREFERENCES = "APP_ALREADY_OPENED_PREFERENCES"
        const val AUDIO_MODE_CHANGED_BY_THIS_APP = "AUDIO_MODE_CHANGED_BY_THIS_APP"
        private const val SHARED_PREFERENCES_TAG: String = "SilentManagerSHARED1230"
    }

    lateinit var eventService: IEventService
    lateinit var userService: IUserService
    lateinit var accountService: IAccountService
    lateinit var invitationService: IInvitationService

    val sharedPreferences: ISharedPreferences by lazy {
        MySharedPreferences(getSharedPreferences(SHARED_PREFERENCES_TAG, Context.MODE_PRIVATE))

    }

    override fun onCreate() {
        super.onCreate()

        configureRepositories()
        //runSilentManagerService()
    }


    fun stopSilentManagerService() {
        GcmNetworkManager.getInstance(this)
            .cancelTask(SILENT_MANAGER_SERVICE, SilentManagerService::class.java)
    }

    fun runSilentManagerService() {
        val flexSecs = 0L // the task can run as early as -15 seconds from the scheduled time
        val updateIntervalsInSeconds = 30L
        val tag = SILENT_MANAGER_SERVICE
        val periodic = PeriodicTask.Builder()
            .setService(SilentManagerService::class.java)
            .setPeriod(updateIntervalsInSeconds)
            .setFlex(flexSecs)
            .setTag(tag)
            .setPersisted(true)
            .setRequiredNetwork(com.google.android.gms.gcm.Task.NETWORK_STATE_ANY)
            .setRequiresCharging(false)
            .setUpdateCurrent(true)
            .build()

        GcmNetworkManager.getInstance(this).schedule(periodic)
    }

    private fun addGeoFenceService() {
        LocationManager.getInstance().getLastLocation(this) {
            it?.let { location ->
                eventService.getEvents(location.latitude, location.longitude,
                    Constants.DEFAULT_RADIUS_SIZE, null, { events ->
                        if (events?.results != null)
                            GeofenceManager().startGeoFenceService(
                                applicationContext,
                                events.results
                            )
                    }, {})
            }
        }
    }

    private fun configureRepositories() {
        val queue = Volley.newRequestQueue(applicationContext)
        val request = HttpRequest(queue, ErrorInterpreter(ErrorMessageProvider(applicationContext)))
        val host = getText(R.string.api_host).toString()

        val eventRepo = EventRepository(request, host)
        val userRepo = UserRepository(request, host)
        val accountRepo = AccountRepository(request, host)
        val invitationRepo = InviteRepository(request, host)

        eventService = EventService(eventRepo)
        userService = UserService(userRepo)
        accountService = AccountService(accountRepo)
        invitationService = InvitationService(invitationRepo)
    }
}