package com.silent_manager.g29.silent_manager.android_components.fragments


import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.android_components.SilentManagerApplication
import com.silent_manager.g29.silent_manager.android_components.helpers.HelperMethods
import com.silent_manager.g29.silent_manager.android_components.input_regex_filters.FilterInputTypedData
import com.silent_manager.g29.silent_manager.android_components.view_models.LogInViewModel
import com.silent_manager.g29.silent_manager.data_layer.models.Token
import kotlinx.android.synthetic.main.content_loading_data.*
import kotlinx.android.synthetic.main.fragment_log_in.*


class LogInFragment : Fragment() {
    private fun getViewModelFactory() = object : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return LogInViewModel(activity?.application as SilentManagerApplication) as T
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_log_in, container, false)
    }

    private fun enableViews(enable: Boolean) {
        log_in_email_value.isEnabled = enable
        log_in_password_value.isEnabled = enable
        login_sign_in_btm.isEnabled = enable
        login_create_accout_btm.isEnabled = enable
    }

    private fun activateLoaderScreen(activate: Boolean) {
        enableViews(!activate)
        if (activate) {
            loading_content_container?.visibility = View.VISIBLE
            loading_data?.text = getText(R.string.login_loader_authenticating)
        } else {
            loading_content_container?.visibility = View.GONE
        }
    }

    override fun onStart() {
        activateLoaderScreen(false)
        init()
        super.onStart()
    }

    private fun init() {
        login_sign_in_btm.setOnClickListener { onSignInClick() }
        login_create_accout_btm.setOnClickListener { onRegisterAccountClick() }

        addInputFilters()
    }

    private fun addInputFilters() {
        log_in_email_value.filters =
            arrayOf(
                FilterInputTypedData(
                    FilterInputTypedData.EMAIL_TYPE_FILTER_REGEX
                ),
                InputFilter.LengthFilter(resources.getInteger(R.integer.small_input_max_size))
            )
        log_in_password_value.filters =
            arrayOf(
                FilterInputTypedData(
                    FilterInputTypedData.PASSWORD_TYPE_FILTER_REGEX
                ),
                InputFilter.LengthFilter(resources.getInteger(R.integer.small_input_max_size))
            )
    }

    private fun onRegisterAccountClick() {
        fragmentManager?.let {
            HelperMethods.changeFragmentContext(it, RegisterFragment(), false)
        }
    }

    private fun onSignInClick() {
        activateLoaderScreen(true)

        val viewModel: LogInViewModel = getViewModel()

        // Observe for token received
        viewModel.getTokenLiveData().observe(this, Observer<Token?> {
            if (it != null)
                onTokenReceived()
            else {
                onError()
            }
        })

        val email = log_in_email_value.text.toString()
        val password = log_in_password_value.text.toString()

        viewModel.logIn(email, password)
    }

    private fun onError() {
        activateLoaderScreen(false)
        Toast.makeText(context, R.string.login_failed_message, Toast.LENGTH_SHORT).show()
    }

    private fun onTokenReceived() {
        fragmentManager?.let {
            HelperMethods.changeFragmentContext(it, ManagementEventsFragment(), false)
        }
    }


    private fun getViewModel(): LogInViewModel {
        return ViewModelProviders
            .of(this, getViewModelFactory())
            .get(LogInViewModel::class.java)
    }
}
