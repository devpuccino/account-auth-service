package com.devpuccino.account.authservice.util

import com.devpuccino.account.authservice.filter.LoggingFilter
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpHeaders
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.stereotype.Component
import org.springframework.util.MultiValueMap
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.text.isNullOrEmpty

@Component
class LoggingUtil(val objectMapper: ObjectMapper) {
    val logger: Logger = LoggerFactory.getLogger(LoggingFilter::class.java)
    fun logRequest(request: ServerHttpRequest, body: String? = null) {
        StringBuilder().also { stringBuilder ->
                stringBuilder.append(" header=[")
                    .append(buildRequestHeader(request.headers).let(objectMapper::writeValueAsString))
                    .append("]")
            if (request.queryParams.isNotEmpty()) {
                stringBuilder.append(" parameter=[")
                    .append(buildRequestParameter(request.queryParams).let(objectMapper::writeValueAsString))
                    .append("]")
            }
            if (!body.isNullOrEmpty()) {
                kotlin.runCatching {
                    objectMapper.readValue<HashMap<String, Any>>(body)
                }.onSuccess { bodyObject ->
                    stringBuilder.append(" body=[")
                        .append(objectMapper.writeValueAsString(bodyObject)).append("]")
                }.onFailure {
                    stringBuilder.append(" body=[").append(body).append("]")
                }
            }
        }.also {
            logger.info(
                "REQUEST method=[{}] path=[{}]{}",
                request.method.name(),
                request.uri.path,
                it.toString()
            )
        }
    }
    fun logResponse(
        startTime: Long,
        response: ServerHttpResponse,
        body: String? = null
    ) {
            StringBuilder().also { stringBuilder ->
                if (!body.isNullOrEmpty()) {
                    stringBuilder.append(" body=[").append(body).append("]")
                }
            }.also {
                logger.info("RESPONSE [{} ms] status=[{}]{}", System.currentTimeMillis()-startTime, response.statusCode?.value(), it.toString())
            }
    }
    private fun buildRequestParameter(parameters: MultiValueMap<String, String>) =
        HashMap<String, Any>().apply {
            parameters.forEach { (key, value) ->
                run {
                    this[key] = when (value.size > 1) {
                        true -> value
                        else -> value[0]
                    }
                }
            }
        }

    private fun buildRequestHeader(headers: HttpHeaders) = HashMap<String, Any>().apply {
        headers.forEach { key, value ->
            run {
                this[key] = when (value.size > 1) {
                    true -> value
                    else -> value[0]
                }
            }
        }
    }
}