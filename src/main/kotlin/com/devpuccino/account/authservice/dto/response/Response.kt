package com.devpuccino.account.authservice.dto.response

import com.devpuccino.account.authservice.contant.Namespace
import com.devpuccino.account.authservice.contant.AuthResponseStatus

open class CommonResponse<T> {
    val code: String
    val message: String
    val namespace: String
    val data: T?

    constructor(code: String, message: String, namespace: String, data: T? = null) {
        this.code = code
        this.message = message
        this.namespace = namespace
        this.data = data
    }

}

data class ErrorResponse(private val responseStatus: AuthResponseStatus) : CommonResponse<Any?>(
    code = responseStatus.code,
    message = responseStatus.message,
    namespace = Namespace.ACCOUNT_AUTH_SERVICE_NAMESPACE
)

