package com.silent_manager.g29.silent_manager.android_components.view_models

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.data_layer.models.PageResult
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import com.silent_manager.g29.silent_manager.data_layer.models.User

class SearchUsersViewModel(applicationContext: SilentManagerApplication) : AndroidViewModel(applicationContext) {
    private val _users: MutableLiveData<Array<User>> = MutableLiveData()

    fun getUsers(): LiveData<Array<User>> = _users

    fun updateUsers(filterEmailName: String) {
        getApplication<SilentManagerApplication>().userService.getUserByParameter(
            filterEmailName,
            ::onUsersReceived,
            ::onUsersReceivedError
        )
    }

    fun onUsersReceived(users: PageResult<User>?) {
        _users.value = users?.results
    }

    fun onUsersReceivedError(error: RequestError) {}
}