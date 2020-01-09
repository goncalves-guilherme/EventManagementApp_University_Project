package com.silent_manager.g29.silent_manager.android_components.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.silent_manager.g29.silent_manager.R
import android.widget.Toast
import android.content.pm.PackageManager
import android.content.ComponentName
import android.content.Intent
import android.support.v7.widget.LinearLayoutManager
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.activities.ContactsListActivity
import com.silent_manager.g29.silent_manager.android_components.adapters.ContactsAdapter
import com.silent_manager.g29.silent_manager.android_components.android_broadcast.PhoneCallReceiver
import com.silent_manager.g29.silent_manager.android_components.managers.ContactsManager
import com.silent_manager.g29.silent_manager.android_components.managers.MyAudioManager
import com.silent_manager.g29.silent_manager.android_components.managers.PermissionManager
import com.silent_manager.g29.silent_manager.android_components.managers.SilentFlowManager
import com.silent_manager.g29.silent_manager.data_layer.models.Contact
import kotlinx.android.synthetic.main.fragment_preferences.*
import kotlin.reflect.KClass


class PreferencesFragment : Fragment() {
    private val TAG = "SETTINGSFRAGMENT"

    companion object {
        const val KEY_CALL_PREFERENCE = "KEYCALLPREF"
        const val KEY_GEOFENCE_PREFERENCE = "GEOFENCEPREFERENCEs"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_preferences, container, false)
    }

    override fun onStart() {
        super.onStart()
        init()
    }

    private fun init() {
        setDefaultValues()

        turn_on_off_automatic_silent_switch.setOnCheckedChangeListener { _, isChecked ->
            onAutomaticSilentSwitch(isChecked)
        }

        turn_on_off_contact_exceptions_switch.setOnCheckedChangeListener { _, isChecked ->
            onContactExceptionsSwitch(isChecked)
        }

        add_contact_exception_btm.setOnClickListener { onAddContactExceptionClick() }
        changeContactExceptionBtm()
        initContactExceptionList()
    }

    private fun changeContactExceptionBtm() {
        val isEnable = turn_on_off_contact_exceptions_switch.isChecked

        add_contact_exception_btm?.isEnabled = isEnable
        preferences_contact_exceptions_list?.isEnabled = isEnable
        preferences_no_exceptions_found?.isEnabled = isEnable
        add_contact_exception_btm?.isEnabled = isEnable
    }

    private fun onAddContactExceptionClick() {
        val intent = Intent(context?.applicationContext, ContactsListActivity::class.java)
        startActivity(intent)
    }

    private fun setDefaultValues() {
        // Setting switch to false if user does not grant permission to read and access to contact and call state.
        context?.let {
            val cm = ContactsManager.getInstance()
            turn_on_off_contact_exceptions_switch.isChecked =
                    cm.isOutGoingCallsPermitted(it) &&
                    getPreference(KEY_CALL_PREFERENCE)

            turn_on_off_automatic_silent_switch.isChecked = getPreference(KEY_GEOFENCE_PREFERENCE)
        }
    }

    private fun initContactExceptionList() {
        this.context?.let {
            val cm = ContactsManager.getInstance()
            val exceptions = cm.getContactExceptions(it)

            val existsExceptions = exceptions.size > 0

            if (existsExceptions)
                addContactsToTheList(exceptions)

            contactExceptionListOrLabel(existsExceptions)
        }
    }

    private fun contactExceptionListOrLabel(exceptionsExists: Boolean) {
        if (exceptionsExists) {
            preferences_contact_exceptions_list.visibility = View.VISIBLE
            preferences_no_exceptions_found.visibility = View.GONE
        } else {
            preferences_contact_exceptions_list.visibility = View.GONE
            preferences_no_exceptions_found.visibility = View.VISIBLE
        }
    }

    private fun onContactClick(contact: Contact, position: Int) {
        if (preferences_contact_exceptions_list?.isEnabled!!) {
            this.context?.let {
                val a = preferences_contact_exceptions_list.adapter as ContactsAdapter
                a.removedContact(position)

                val cm = ContactsManager.getInstance()
                cm.removeContactException(it, contact)

                contactExceptionListOrLabel(cm.getContactExceptions(it).size > 0)
            }
        }
    }

    private fun addContactsToTheList(contacts: MutableList<Contact>) {
        val viewManager = LinearLayoutManager(this.activity)
        val viewAdapter = ContactsAdapter(contacts, this::onContactClick)

        preferences_contact_exceptions_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }


    private fun onContactExceptionsSwitch(isChecked: Boolean) {
        if (isChecked) {
            context?.let {
                if (
                    requestOutGoingCalls() &&
                    requestReadContactPermissionIfNotPermittedYet() &&
                    requestNotificationPolicyAccessOfNotPermittedYet()
                ) {
                    startContactExceptionsServices()
                } else {
                    return
                }
            }
        } else {
            stopContactExeceptionsServices()
        }

        changeContactExceptionBtm()
        savePreference(KEY_CALL_PREFERENCE, isChecked)
    }

    private fun requestOutGoingCalls(): Boolean {
        val cm = ContactsManager.getInstance()
        context?.let {
            if (!cm.isOutGoingCallsPermitted(it)) {
                cm.requestOutGoingCallsPermission(this)
                return false
            }
        }
        return true
    }

    override fun startActivityForResult(intent: Intent?, requestCode: Int) {
        super.startActivityForResult(intent, requestCode)
        when (requestCode) {
            PermissionManager.PermissionCodes.NOTIFICATION_POLICY_ACCESS_PERMISSION.ordinal -> {
                val am = MyAudioManager.getInstance()
                context?.let {
                    if (am.isNotificationPolicyAccessPermitted(it)) {
                        onContactExceptionPermissionReceived(intArrayOf(PackageManager.PERMISSION_GRANTED))
                    }
                }
                return
            }

        }
    }

    private fun requestNotificationPolicyAccessOfNotPermittedYet(): Boolean {
        val am = MyAudioManager.getInstance()
        context?.let {
            if (!am.isNotificationPolicyAccessPermitted(it)) {
                am.requestNotificationPolicyAccessPermitted(this)
                return false
            }
        }
        return true
    }

    private fun requestReadContactPermissionIfNotPermittedYet(): Boolean {
        val cm = ContactsManager.getInstance()
        context?.let {
            if (!cm.isReadContactPermitted(it)) {
                cm.requestReadContactPermission(this)
                return false
            }
        }
        return true
    }

    private fun getPreference(key: String): Boolean {
        val app = context?.applicationContext as SilentManagerApplication
        val data: String = app.sharedPreferences.getStringData(key)

        return data.toBoolean()
    }

    private fun savePreference(key: String, isChecked: Boolean) {
        val app = context?.applicationContext as SilentManagerApplication
        app.sharedPreferences.putStringData(key, isChecked.toString())
    }

    private fun onAutomaticSilentSwitch(isChecked: Boolean) {
        if (isChecked) {
            startGeofenceServices()
            val sm = SilentFlowManager.getInstance()
            sm.invalidateData()
        } else {
            stopGeonfeceServices()
        }

        savePreference(KEY_GEOFENCE_PREFERENCE, isChecked)
    }

    private fun startGeofenceServices() {
        val app = context?.applicationContext as SilentManagerApplication
        app.runSilentManagerService()
        showToastMessage(R.string.preferences_enable_geofence_service_message)
    }

    private fun stopGeonfeceServices() {
        val app = context?.applicationContext as SilentManagerApplication
        app.stopSilentManagerService()
        showToastMessage(R.string.preferences_disable_geofence_service_message)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PermissionManager.PermissionCodes.OUT_GOING_CALLS_PERMISSION.ordinal -> {
                onContactExceptionPermissionReceived(grantResults)
                return
            }
            PermissionManager.PermissionCodes.NOTIFICATION_POLICY_ACCESS_PERMISSION.ordinal -> {
                onContactExceptionPermissionReceived(grantResults)
                return
            }
            PermissionManager.PermissionCodes.READ_CONTACTS_PERMISSION.ordinal -> {
                onContactExceptionPermissionReceived(grantResults)
                return
            }
            PermissionManager.PermissionCodes.NOTIFICATION_POLICY_ACCESS_PERMISSION.ordinal -> {
                onContactExceptionPermissionReceived(grantResults)
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    private fun onContactExceptionPermissionReceived(grantResults: IntArray) {
        if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
            onContactExceptionsSwitch(true)
        } else {
            turn_on_off_contact_exceptions_switch.isChecked = false
            onContactExceptionsSwitch(false)
            showToastMessage(R.string.preferences_contact_permission_disable)
        }
    }

    private fun stopContactExeceptionsServices() {
        enableComponent(false, PhoneCallReceiver::class)
        showToastMessage(R.string.preferences_disable_contact_exception_service_message)
    }

    private fun startContactExceptionsServices() {
        enableComponent(true, PhoneCallReceiver::class)
        showToastMessage(R.string.preferences_enable_contact_exception_service_message)
    }

    private fun showToastMessage(stringId: Int) {
        Toast.makeText(context, stringId, Toast.LENGTH_SHORT).show()
    }

    private fun <T : Any> enableComponent(enable: Boolean, kclass: KClass<T>) {
        val flag = if (enable)
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED
        else
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED

        val component = ComponentName(context, kclass.java)

        activity?.packageManager?.setComponentEnabledSetting(
            component, flag,
            PackageManager.DONT_KILL_APP
        )
    }
}
