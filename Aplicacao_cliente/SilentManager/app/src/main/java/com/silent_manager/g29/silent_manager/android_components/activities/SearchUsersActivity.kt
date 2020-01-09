package com.silent_manager.g29.silent_manager.android_components.activities

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.adapters.SearchUsersAdapter
import com.silent_manager.g29.silent_manager.android_components.input_regex_filters.FilterInputTypedData
import com.silent_manager.g29.silent_manager.android_components.view_models.SearchUsersViewModel
import com.silent_manager.g29.silent_manager.data_layer.models.User
import kotlinx.android.synthetic.main.content_search_users.*

class SearchUsersActivity : AppCompatActivity() {
    private fun getViewModelFactory() = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchUsersViewModel(application as SilentManagerApplication) as T
        }
    }

    companion object {
        const val USER_KEY = "USEREKEY"
        const val AUTHOR_ID_KEY = "AUTHORUSERIDKEY"
        const val INVITED_USERS_NAMES_LIST = "USERSNAMESLISTNSDIJ34"
        const val INVITED_EVENT_ID = "INVITED_EVENT_ID"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_users)

        init()
    }

    private fun init() {
        email_name_txt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filter = s.toString()
                val wasLetterRemoved = count < before

                if (wasLetterRemoved && count == 0) {
                    showEmptyUsersWarning(true)
                }

                // Obtain a new list of users only if user type a new sequence with 3 characters
                if (count == 3 && !wasLetterRemoved) {
                    updateUsers(filter)
                } else if (count >= 3) {

                    // Get new users.
                    val usersFiltered = getNewUsersList(filter, !wasLetterRemoved)

                    // Refresh list with new results
                    if (usersFiltered != null) {

                        // Show empty users warning if no users were found.
                        if (!usersFiltered.isNotEmpty())
                            showEmptyUsersWarning(true)
                        else
                            showEmptyUsersWarning(false)

                        updateUsersList(usersFiltered)
                    }
                }
            }

        })

        email_name_txt.filters = arrayOf(FilterInputTypedData(FilterInputTypedData.BASIC_NAME_TYPE_FILTER_REGEX))
        getViewModel().getUsers().observe(this, Observer<Array<User>> {
            onReceivedUsers(it)
        })
    }

    private fun filterUserByName(name: String, users: Array<User>?): Array<User> {
        val mutableList = mutableListOf<User>()
        users?.filter {
            it.name?.startsWith(name, ignoreCase = true)!!
        }?.let { mutableList.addAll(it) }

        return mutableList.toTypedArray()
    }

    private fun getNewUsersList(filter: String, newLetterTyped: Boolean): Array<User>? {
        if (newLetterTyped) {
            // If user types new letter only filters from the list already displayed
            // this saves process time
            return filterUserByName(filter, getBindUserAdapter()?.getCurrentUsersDisplayed())
        }
        // Get the list of users saved in viewmodel that were loaded from server
        val usersList = getViewModel().getUsers().value
        // Filter by the string user inputs
        return filterUserByName(filter, usersList)
    }

    private fun updateUsersList(users: Array<User>) {
        // update user's list
        val searchUsersAdapter = getBindUserAdapter()
        searchUsersAdapter?.filterUserListWithNewList(users)
    }

    private fun getBindUserAdapter(): SearchUsersAdapter? {
        if (search_users_list_container.adapter != null)
            return search_users_list_container.adapter as SearchUsersAdapter

        return null
    }


    private fun updateUsers(searchName: String) {
        getViewModel().updateUsers(searchName)
    }

    private fun showEmptyUsersWarning(toShow: Boolean) {
        if (toShow) {
            search_users_not_found_txt.visibility = View.VISIBLE
            search_users_list_container.visibility = View.GONE
        } else {
            search_users_not_found_txt.visibility = View.GONE
            search_users_list_container.visibility = View.VISIBLE
        }
    }

    private fun usersWithoutInvitation() {}

    private fun onReceivedUsers(users: Array<User>?) {
        if (users?.isNotEmpty() != null && users.isNotEmpty()) {
            val usersFilter = intent.getStringArrayExtra(INVITED_USERS_NAMES_LIST)
            val authorId = intent.getIntExtra(AUTHOR_ID_KEY, -1)

            val filteredUsers =
                users.filter { !(it.id != null && it.id == authorId) || !usersFilter.contains(it.name) }?.toTypedArray()

            if (filteredUsers.isNullOrEmpty()) {
                return showEmptyUsersWarning(true)
            }

            showEmptyUsersWarning(false)
            buildUsersList(filteredUsers)
        } else {
            showEmptyUsersWarning(true)
        }
    }

    private fun getViewModel(): SearchUsersViewModel {
        return ViewModelProviders
            .of(this, getViewModelFactory())
            .get(SearchUsersViewModel::class.java)
    }

    private fun onUserClick(user: User, index: Int) {
        val eventId = intent.getIntExtra(INVITED_EVENT_ID, -1)
        intent.putExtra(USER_KEY, user)
        intent.putExtra(INVITED_EVENT_ID, eventId)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun buildUsersList(users: Array<User>) {
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = SearchUsersAdapter(users.toMutableList(), this::onUserClick)

        search_users_list_container.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

}
