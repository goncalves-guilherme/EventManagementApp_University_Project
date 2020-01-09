package com.silent_manager.g29.silent_manager.android_components.managers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.provider.ContactsContract
import android.support.v4.app.Fragment
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.data_layer.models.Contact

class ContactsManager private constructor() {
    companion object {
        private val contactsManager by lazy { ContactsManager() }
        fun getInstance() = contactsManager
        const val CONTACT_EXCEPTION_LIST = "CONTACT_EXCEPTION_LIST"
        const val CONTACT_FORMAT_SEPARATION_TOKEN = "|"
        const val CONTACT_FORMAT_NAME_NUMBER = ":"
    }

    fun isOutGoingCallsPermitted(context: Context): Boolean {
        val pm = PermissionManager.getInstance()
        return pm.doesUserAllowPermission(Manifest.permission.PROCESS_OUTGOING_CALLS, context)
    }

    fun requestOutGoingCallsPermission(fragment: Fragment) {
        val pm = PermissionManager.getInstance()
        pm.requestUserPermission(
            PermissionManager.PermissionCodes.OUT_GOING_CALLS_PERMISSION.ordinal,
            Manifest.permission.PROCESS_OUTGOING_CALLS, fragment
        )
    }

    fun isReadContactPermitted(context: Context): Boolean {
        val pm = PermissionManager.getInstance()
        return pm.doesUserAllowPermission(Manifest.permission.READ_CONTACTS, context)
    }

    fun requestReadContactPermission(fragment: Fragment) {
        val pm = PermissionManager.getInstance()
        pm.requestUserPermission(
            PermissionManager.PermissionCodes.READ_CONTACTS_PERMISSION.ordinal,
            Manifest.permission.READ_CONTACTS, fragment
        )
    }

    fun getContacts(context: Context): MutableList<Contact> {
        val contactList: MutableList<Contact> = mutableListOf<Contact>()

        val cr = context.contentResolver
        val cur = cr.query(
            ContactsContract.Contacts.CONTENT_URI, null, null, null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (cur?.count ?: 0 > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID))
                val name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id), null
                    )
                    while (pCur!!.moveToNext()) {
                        val phoneNo = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER
                            )
                        )
                        val newContact = Contact(name, phoneNo)
                        contactList.add(newContact)
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()

        return contactList
            .distinctBy { it.number }
            .toMutableList()
    }

    private fun contactToString(contact: Contact): String {
        return "${contact.name}$CONTACT_FORMAT_NAME_NUMBER${contact.number}$CONTACT_FORMAT_SEPARATION_TOKEN"
    }

    private fun contactListToString(contacts: List<Contact>): String {
        var content = ""
        contacts.forEach {
            content += contactToString(it)
        }
        return content
    }


    private fun removeWhiteSpaces(value: String): String {
        return value.replace("\\s".toRegex(), "")
    }

    fun saveContact(context: Context, contact: Contact) {
        val contactFormat =
            "${contact.name}$CONTACT_FORMAT_NAME_NUMBER${contact.number}$CONTACT_FORMAT_SEPARATION_TOKEN"
        val contacts = getContactExceptionsString(context)
        val lastContactList = contacts + contactFormat

        saveToPreferences(context, lastContactList)
    }

    private fun saveToPreferences(context: Context, data: String) {
        val dataToSave = removeWhiteSpaces(data)
        val app = context.applicationContext as SilentManagerApplication
        app.sharedPreferences.putStringData(CONTACT_EXCEPTION_LIST, dataToSave)
    }


    private fun saveContactList(context: Context, contacts: List<Contact>) {
        val stringToSave = contactListToString(contacts)
        saveToPreferences(context, stringToSave)
    }

    fun removeContactException(context: Context, contact: Contact) {
        val exceptions = getContactExceptions(context)
        exceptions.removeAll {
            it.number == contact.number
        }

        saveContactList(context, exceptions)
    }

    fun getContactExceptions(context: Context): MutableList<Contact> {
        val exceptions = mutableListOf<Contact>()
        val exceptionsStringFormat = getContactExceptionsString(context)

        val contactTokens = exceptionsStringFormat.split(CONTACT_FORMAT_SEPARATION_TOKEN)

        contactTokens.forEach { token ->
            val contactT = token.split(CONTACT_FORMAT_NAME_NUMBER)
            if (contactT.size == 2) {

                val name = contactT[0]
                val number = contactT[1]

                exceptions.add(Contact(name, number))
            }
        }

        return exceptions
    }

    fun getContactExceptionsString(context: Context): String {
        val app = context.applicationContext as SilentManagerApplication
        return app.sharedPreferences.getStringData(CONTACT_EXCEPTION_LIST)
    }
}