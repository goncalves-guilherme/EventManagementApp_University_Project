package com.silent_manager.g29.silent_manager.android_components.view_models

import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.managers.TokenManager
import com.silent_manager.g29.silent_manager.data_layer.models.RequestError
import com.silent_manager.g29.silent_manager.data_layer.models.Token


class LogInViewModel(applicationContext: SilentManagerApplication) : AndroidViewModel(applicationContext) {

    private val _tokenLiveData: MutableLiveData<Token> = MutableLiveData<Token>()
    fun getTokenLiveData(): LiveData<Token?> = _tokenLiveData

    fun registerUser(email: String, password: String, name: String) {
        getApplication<SilentManagerApplication>().accountService.registerUser(
            name,
            email,
            password,
            ::onSuccess,
            ::onError
        )
    }

    fun logIn(email: String, password: String) {
        getApplication<SilentManagerApplication>().accountService.login(
            email,
            password,
            ::onSuccess,
            ::onError
        )
    }

    private fun onSuccess(token: Token?) {
        token?.let {
            val tm = TokenManager.getInstance()
            val app = getApplication<SilentManagerApplication>().applicationContext
            tm.saveToken(it, app)
            _tokenLiveData.value = token
        }
    }

    private fun onError(error: RequestError) {
        _tokenLiveData.value = null
    }
}