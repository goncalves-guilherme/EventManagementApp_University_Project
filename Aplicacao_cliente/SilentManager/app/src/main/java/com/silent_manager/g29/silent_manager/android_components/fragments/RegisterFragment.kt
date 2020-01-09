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
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {
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
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onStart() {
        init()
        super.onStart()
    }

    private fun init() {
        activateLoaderScreen(false)
        register_register_btm.setOnClickListener {
            onRegisterAccountClick()
        }

        addInputFilters()
    }

    private fun enableViews(enable: Boolean) {
        register_name_input?.isEnabled = enable
        register_email_input?.isEnabled = enable
        register_password_input?.isEnabled = enable
        register_register_btm?.isEnabled = enable
    }

    private fun activateLoaderScreen(activate: Boolean) {
        enableViews(!activate)
        if (activate) {
            loading_content_container?.visibility = View.VISIBLE
            loading_data?.text = getText(R.string.register_loader_creating_account)
        } else {
            loading_content_container?.visibility = View.GONE
        }
    }


    private fun addInputFilters() {
        register_name_input.filters =
            arrayOf(
                FilterInputTypedData(
                    FilterInputTypedData.BASIC_NAME_TYPE_FILTER_REGEX
                ),
                InputFilter.LengthFilter(resources.getInteger(R.integer.small_input_max_size))
            )
        register_email_input.filters =
            arrayOf(
                FilterInputTypedData(
                    FilterInputTypedData.EMAIL_TYPE_FILTER_REGEX
                ),
                InputFilter.LengthFilter(resources.getInteger(R.integer.small_input_max_size))
            )
        register_password_input.filters =
            arrayOf(
                FilterInputTypedData(
                    FilterInputTypedData.PASSWORD_TYPE_FILTER_REGEX
                ),
                InputFilter.LengthFilter(resources.getInteger(R.integer.small_input_max_size))
            )
    }

    private fun onRegisterAccountClick() {
        activateLoaderScreen(true)
        val viewModel: LogInViewModel = getViewModel()

        // Observe for token received
        viewModel.getTokenLiveData().observe(this, Observer<Token?> {
            if (it != null) {
                onSuccessfullAccountCreating()
            } else {
                onFailedToCreate()
            }
        })

        val name = register_name_input.text.toString()
        val email = register_email_input.text.toString()
        val password = register_password_input.text.toString()

        viewModel.registerUser(email, password, name)
    }

    private fun onFailedToCreate() {
        activateLoaderScreen(false)
        Toast.makeText(context, R.string.register_failed_to_create_account, Toast.LENGTH_SHORT)
            .show()
    }

    private fun onSuccessfullAccountCreating() {
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
