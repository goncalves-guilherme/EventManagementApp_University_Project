package com.silent_manager.g29.silent_manager.android_components.managers

import android.content.Context
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.data_layer.models.Token


class TokenManager private constructor() {
    companion object {
        private const val TOKEN_KEY = "tkkoenKey"
        private const val TOKEN_DATE_KEY = "create"
        private val tokenManager by lazy { TokenManager() }
        fun getInstance() = tokenManager
    }

    fun getAccessToken(context: Context): String {
        val app = context.applicationContext as SilentManagerApplication
        return app.sharedPreferences.getStringData(TOKEN_KEY)
    }

    fun getToken(context: Context): Token {
        val app = context.applicationContext as SilentManagerApplication
        val token = app.sharedPreferences.getStringData(TOKEN_KEY)
        val expired = app.sharedPreferences.getStringData(TOKEN_DATE_KEY)
        return Token(AccessToken = token, Expiration = expired)
    }

    fun saveToken(accessToken: Token, context: Context) {
        val app = context.applicationContext as SilentManagerApplication
        app.sharedPreferences.putStringData(TOKEN_KEY, accessToken.AccessToken.orEmpty())
        app.sharedPreferences.putStringData(TOKEN_DATE_KEY, accessToken.Expiration.orEmpty())
    }

    fun invalidateToken(context: Context) {
        val app = context.applicationContext as SilentManagerApplication
        app.sharedPreferences.putStringData(TOKEN_KEY, "")
        app.sharedPreferences.putStringData(TOKEN_DATE_KEY, "")
    }

    fun saveAccessToken(accessToken: String, context: Context) {
        val app = context.applicationContext as SilentManagerApplication
        app.sharedPreferences.putStringData(TOKEN_KEY, accessToken)
    }
}