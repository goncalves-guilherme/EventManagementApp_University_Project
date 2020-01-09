package com.silent_manager.g29.silent_manager.android_components.helpers

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import com.silent_manager.g29.silent_manager.R
import java.text.SimpleDateFormat
import java.util.*


object HelperMethods {
    fun changeFragmentContext(
        fragmentManager: FragmentManager,
        newFragment: Fragment, addBackStack: Boolean
    ) {

        val transaction = fragmentManager?.beginTransaction()

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack if needed
        transaction?.replace(R.id.fragment_container, newFragment)

        if (addBackStack)
            transaction?.addToBackStack(null)

        // Commit the transaction
        transaction?.commit()
    }

    fun getFormattedDoubleToString(n: Double?): String {
        return "%.6f".format(n)
    }

    fun getDateFormatted(date: Date?): String {
        if (date == null) return ""

        val datePattern = "yyyy/MM/dd HH:mm"
        return SimpleDateFormat(datePattern).format(date)
    }
}