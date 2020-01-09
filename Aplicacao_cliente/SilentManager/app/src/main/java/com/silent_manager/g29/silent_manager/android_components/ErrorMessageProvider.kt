package com.silent_manager.g29.silent_manager.android_components

import android.content.Context
import android.content.res.Resources
import com.silent_manager.g29.silent_manager.R
import com.silent_manager.g29.silent_manager.data_layer.request.ErrorInterpreter
import com.silent_manager.g29.silent_manager.data_layer.request.IErrorMessageProvider

class ErrorMessageProvider(private val app: Context): IErrorMessageProvider{
    override fun getErrorMessage(errorCode: ErrorInterpreter.ErrorCode): String {
        return when(errorCode){
            ErrorInterpreter.ErrorCode.NEED_AUNTHENTICATION_ERROR -> codeToString(R.string.Need_Authentication_Error)
            else -> codeToString(R.string.Generic_Error)
        }
    }

    private fun codeToString(code: Int): String{
        return app.getString(code)
    }

}