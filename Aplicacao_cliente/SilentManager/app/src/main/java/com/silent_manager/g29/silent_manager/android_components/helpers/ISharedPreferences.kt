package com.silent_manager.g29.silent_manager.android_components.helpers

interface ISharedPreferences {
    fun putStringData(key: String, data: String)
    fun getStringData(key: String): String

    fun putBooleanData(key: String, data: Boolean)
    fun getBooleanData(key: String): Boolean
}