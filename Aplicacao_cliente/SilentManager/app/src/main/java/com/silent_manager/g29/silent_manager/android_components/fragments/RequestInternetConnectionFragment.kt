package com.silent_manager.g29.silent_manager.android_components.fragments


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.activities.MainActivity
import com.silent_manager.g29.silent_manager.android_components.managers.WifiManager
import kotlinx.android.synthetic.main.fragment_request_internet_connection.*

class RequestInternetConnectionFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_request_internet_connection, container, false)
    }


    override fun onStart() {
        super.onStart()

        request_internet_connection_btm.setOnClickListener { requestInternet() }
    }

    private fun requestInternet() {
        val wm = WifiManager.getInstance()
        context?.let {
            if (wm.isInternetEnable(it)) {
                val intent = Intent(context, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            } else {
                context?.let { con ->
                    Toast.makeText(
                        con,
                        R.string.request_internet_no_connection_yet,
                        Toast.LENGTH_SHORT
                    ).show()
                }

            }
        }
    }
}
