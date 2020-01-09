package com.silent_manager.g29.silent_manager.android_components.helpers

import android.content.SharedPreferences

class MySharedPreferences(private val sharedPreferences: SharedPreferences) : ISharedPreferences {
    override fun putBooleanData(key: String, data: Boolean) {
        val editor = sharedPreferences.edit()

        editor.putBoolean(key, data)

        editor.apply()
    }

    override fun getBooleanData(key: String): Boolean {
        return sharedPreferences.getBoolean(key, false)
    }

    private val empty = ""

    override fun putStringData(key: String, data: String) {
        val editor = sharedPreferences.edit()

        editor.putString(key, data)

        editor.apply()
    }

    override fun getStringData(key: String): String {
        return sharedPreferences.getString(key, empty)
    }
}