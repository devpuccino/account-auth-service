package com.devpuccino.account.authservice.filter

import io.micrometer.tracing.Tracer
import org.springframework.core.Ordered
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.*

@Component
class TracingFilter( val tracer: Tracer) : WebFilter, Ordered {
    companion object {
        const val CORRELATION_ID_PREFIX = "ACCAUTH-"
        const val CORRELATION_ID_CONTEXT_NAME = "correlationId"
    }

    override fun getOrder(): Int = Ordered.HIGHEST_PRECEDENCE
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        this.tracer.createBaggageInScope(CORRELATION_ID_CONTEXT_NAME, getCorrelationId(exchange.request))
        return chain.filter(exchange)

    }

    fun getCorrelationId(request: ServerHttpRequest): String {
        val headerValue = request.headers[CORRELATION_ID_CONTEXT_NAME]?.firstOrNull()
        return if (!headerValue.isNullOrBlank()) headerValue else generateCorrelationId()
    }

    fun generateCorrelationId(): String = "${CORRELATION_ID_PREFIX}${UUID.randomUUID().toString().replace("-", "")}"


}