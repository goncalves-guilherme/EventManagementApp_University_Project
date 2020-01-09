package com.silent_manager.g29.silent_manager.android_components.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.adapters.ContactsAdapter
import com.silent_manager.g29.silent_manager.android_components.managers.ContactsManager
import com.silent_manager.g29.silent_manager.android_components.view_models.ContactListViewModel
import com.silent_manager.g29.silent_manager.data_layer.models.Contact
import kotlinx.android.synthetic.main.activity_contacts_list.*
import kotlinx.android.synthetic.main.content_loading_data.*


class ContactsListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contacts_list)
    }

    override fun onStart() {
        super.onStart()
        activateContactLoaderScreen(true)
        init()
    }

    private fun init() {
        addContactsToTheList()
        close_contact_list.setOnClickListener { closeList() }
    }

    private fun activateContactLoaderScreen(activate: Boolean) {
        if (activate) {
            loading_content_container.visibility = View.VISIBLE
            contact_list_layout.visibility = View.GONE
            loading_data.setText(R.string.loading_contacts_info)
        } else {
            loading_content_container.visibility = View.GONE
            contact_list_layout.visibility = View.VISIBLE
        }
    }

    private fun getViewModel(): ContactListViewModel {
        return ViewModelProviders.of(this)[ContactListViewModel::class.java]
    }

    private fun addContactsToTheList() {
        val viewModel = getViewModel()
        viewModel.contactsListLiveData().observe(this, Observer { c ->
            if(c != null && c.size > 0){
                hideContactListRender(false)
                buildContactList(c)
            }else{
                hideContactListRender(true)
            }
            activateContactLoaderScreen(false)
        })
        viewModel.updateContactsList(this)
    }

    private fun hideContactListRender(hide: Boolean){
        if(hide){
            no_contacts_found.visibility = View.VISIBLE
            contact_list.visibility = View.GONE
        }
        else{
            no_contacts_found.visibility = View.GONE
            contact_list.visibility = View.VISIBLE
        }
    }

    private fun buildContactList(contacts: MutableList<Contact>){
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = ContactsAdapter(contacts, this::onContactClick)

        contact_list.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    private fun closeList() {
        finish()
    }

    private fun onContactClick(contact: Contact, position: Int) {

        val list = contact_list.adapter as ContactsAdapter
        list.removedContact(position)

        val cm = ContactsManager.getInstance()
        cm.saveContact(this, contact)

    }

}
