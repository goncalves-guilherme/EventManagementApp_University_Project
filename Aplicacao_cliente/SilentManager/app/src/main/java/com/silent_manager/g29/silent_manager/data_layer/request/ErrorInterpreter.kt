package com.silent_manager.g29.silent_manager.data_layer.request

import com.silent_manager.g29.silent_manager.data_layer.models.RequestError

class ErrorInterpreter(override val errorMessageProvider: IErrorMessageProvider) : IErrorInterpreter {
    enum class ErrorCode {
        UNKNOWN_ERROR, BAD_REQUEST_ERROR, NEED_AUNTHENTICATION_ERROR, UNEXPECTED_RESPONSE_FORMAT
    }

    override fun convertToApplicationError(httpCode: Int?): RequestError {
        var error = RequestError(ErrorCode.UNKNOWN_ERROR, "FAILED")

        when (httpCode) {
            401 -> error = RequestError(
                ErrorCode.NEED_AUNTHENTICATION_ERROR,
                errorMessageProvider.getErrorMessage(ErrorCode.NEED_AUNTHENTICATION_ERROR)
            )
        }

        return error
    }
}