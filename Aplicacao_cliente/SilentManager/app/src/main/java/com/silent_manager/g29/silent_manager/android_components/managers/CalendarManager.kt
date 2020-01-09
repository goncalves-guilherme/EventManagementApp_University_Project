package com.silent_manager.g29.silent_manager.android_components.managers

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.CalendarContract
import com.silent_manager.g29.silent_manager.android_components.android_broadcast.CalendarChangesReceiver
import com.silent_manager.g29.silent_manager.data_layer.models.Event
import java.util.*


class CalendarManager private constructor() {
    private val eventsAttributes = arrayOf(
        CalendarContract.Events.CALENDAR_ID,
        CalendarContract.Events.TITLE,
        CalendarContract.Events.DESCRIPTION,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DTEND
    )

    private enum class EventAttribute {
        CALENDAR_ID, TITLE, DESCRIPTION, DTSTART, DTEND
    }

    companion object {
        private val calendarManager by lazy { CalendarManager() }
        fun getInstance() = calendarManager
    }

    fun setListenForCalendarChangesOn(context: Context?) {

        val intentFilter = IntentFilter()
        intentFilter.addAction(Intent.ACTION_PROVIDER_CHANGED)

        val calendarChangesReceiver =
            CalendarChangesReceiver()

        context?.registerReceiver(calendarChangesReceiver, intentFilter)
    }

    fun setListenForCalendarChangesOff() {

    }


    fun isReadCalendarPermitted(context: Context): Boolean {
        val pm = PermissionManager.getInstance()
        return pm.doesUserAllowPermission(Manifest.permission.READ_CALENDAR, context)
    }

    fun requestCalendarPermission(fragment: android.support.v4.app.Fragment) {
        val pm = PermissionManager.getInstance()
        pm.requestUserPermission(PermissionManager.PermissionCodes.READ_CALENDAR.ordinal,
            Manifest.permission.READ_CALENDAR,
            fragment)
    }

    private fun getTodayEventsSelector(): String {
        val startTime = Calendar.getInstance()

        startTime.set(Calendar.HOUR_OF_DAY, 0)
        startTime.set(Calendar.MINUTE, 0)
        startTime.set(Calendar.SECOND, 0)


        val s = startTime.toString()

        val endTime = Calendar.getInstance()
        endTime.add(Calendar.DATE, 1)

        return "((${CalendarContract.Events.DTSTART} >= ${startTime.timeInMillis}) AND " +
                "(${CalendarContract.Events.DTSTART} <= ${endTime.timeInMillis})"// AND"/* +
        //" AND ( deleted != 1 ))"
    }

    fun getAndroidCalendarEvents(context: Context) {
        val allowedReadCalendar = isReadCalendarPermitted(context)

        if (allowedReadCalendar) {
            val events: ArrayList<Event> = ArrayList()
            val selection = getTodayEventsSelector()

            val startTime = Calendar.getInstance()

            startTime.set(Calendar.HOUR_OF_DAY, 0)
            startTime.set(Calendar.MINUTE, 0)
            startTime.set(Calendar.SECOND, 0)
            val cursor = context.contentResolver?.query(
                CalendarContract.Events.CONTENT_URI,
                eventsAttributes,
                selection,
                null, null
            )

            cursor?.moveToFirst()

            cursor?.count?.let { size ->
                for (i in 0 until size) {
                    val event = Event(
                        name = cursor.getString(EventAttribute.TITLE.ordinal),
                        eventId = null,
                        state = null,
                        startDate = Date(cursor.getLong(EventAttribute.DTSTART.ordinal)),
                        endDate = Date(cursor.getLong(EventAttribute.DTEND.ordinal)),
                        radius = null,
                        author = null,
                        description = cursor.getString(EventAttribute.DESCRIPTION.ordinal),
                        location = null,
                        category = ""
                    )
                    events.add(event)
                    cursor.moveToNext()
                }
            }
            val i = 0
        }
    }
}