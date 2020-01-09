package com.silent_manager.g29.silent_manager.android_components.view_models

import android.arch.lifecycle.*
import android.content.Context
import android.os.AsyncTask
import com.silent_manager.g29.silent_manager.android_components.helpers.runAsync
import com.silent_manager.g29.silent_manager.android_components.managers.ContactsManager
import com.silent_manager.g29.silent_manager.data_layer.models.Contact

class ContactListViewModel : ViewModel() {
    private val _contactList: MutableLiveData<MutableList<Contact>?> = MutableLiveData()
    fun contactsListLiveData(): LiveData<MutableList<Contact>?> = _contactList

    fun updateContactsList(context: Context) {
        runAsync {
            getContactsList(context)
        }.andThen {
            _contactList.value = it
        }
    }

    private fun getContactsList(context: Context): MutableList<Contact> {
        val cm = ContactsManager.getInstance()
        val contactsList: MutableList<Contact> = mutableListOf()

        if (cm.isReadContactPermitted(context)) {
            val contactExceptions = cm.getContactExceptions(context)

            val contacts = cm.getContacts(context)
            contacts.removeAll { c ->
                contactExceptions.firstOrNull { e ->
                    c.number == e.number
                } != null
            }

            contactsList.addAll(contacts)
        }

        return contactsList
    }
}