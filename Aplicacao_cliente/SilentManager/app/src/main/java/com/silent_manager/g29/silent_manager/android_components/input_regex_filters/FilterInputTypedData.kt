package com.silent_manager.g29.silent_manager.android_components.input_regex_filters

import android.text.InputFilter
import android.text.Spanned
import java.util.regex.Matcher
import java.util.regex.Pattern

class FilterInputTypedData private constructor(
    private val pattern: Pattern
) : InputFilter {

    constructor(regex: String) : this(Pattern.compile(regex))

    companion object {
        const val BASIC_NAME_TYPE_FILTER_REGEX = "[A-Za-z_\" \".-\\R]"
        const val EMAIL_TYPE_FILTER_REGEX = "[A-Za-z_\"\".-\\R]"
        const val PASSWORD_TYPE_FILTER_REGEX = "[A-z0-9.@]"
        const val EMAIL2_TYPE_FILTER_REGEX = "^[A-z0-9_.-]+@[A-z0-9]+.+[a-zA-Z]"
        const val LOCATION_TYPE_FILTER_REGEX = "[A-Za-z0-9.\" \"]"
    }

    override fun filter(
        source: CharSequence?,
        start: Int,
        end: Int,
        dest: Spanned?,
        dstart: Int,
        dend: Int
    ): CharSequence? {

        val matcher: Matcher = pattern.matcher(source)
        if (matcher.find()) {
            return source
        }


        return ""
    }

}