package com.devpuccino.account.authservice.handler

import com.devpuccino.account.authservice.contant.AuthResponseStatus
import com.devpuccino.account.authservice.dto.response.CommonResponse
import com.devpuccino.account.authservice.dto.response.ErrorResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.reactive.resource.NoResourceFoundException
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestControllerAdvice
class ServiceExceptionHandler {
    companion object{
        val logger: Logger = LoggerFactory.getLogger(ServiceExceptionHandler::class.java)
    }
    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleException(exception: Exception): Mono<ErrorResponse> {
        logger.error(AuthResponseStatus.UNEXPECTED_ERROR.message,exception)
        return ErrorResponse(AuthResponseStatus.UNEXPECTED_ERROR).toMono()
    }
    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleNoResourceFoundException(exception: NoResourceFoundException):Mono<ErrorResponse>{
        return ErrorResponse(AuthResponseStatus.API_FORBIDDEN_ERROR).toMono()
    }
}