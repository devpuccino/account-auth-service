package com.devpuccino.account.authservice.filter

import com.devpuccino.account.authservice.util.LoggingUtil
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.core.Ordered
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpRequestDecorator
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.http.server.reactive.ServerHttpResponseDecorator
import org.springframework.stereotype.Component
import org.springframework.util.StreamUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.ServerWebExchangeDecorator
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.channels.Channels

@Component
class LoggingFilter(private val loggingUtil: LoggingUtil) : WebFilter, Ordered {
    override fun getOrder(): Int = Integer.MIN_VALUE

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        var startTime = 0L
        return object : ServerWebExchangeDecorator(exchange) {
            override fun getRequest(): ServerHttpRequest {
                return LoggingRequestDecorator(loggingUtil, super.getRequest())
            }

            override fun getResponse(): ServerHttpResponse {
                return LoggingResponseDecorator(loggingUtil,super.getResponse())
            }
        }.let { exchangeDecorator ->
            chain.filter(exchangeDecorator).doFirst {
                startTime = System.currentTimeMillis()
                if (exchangeDecorator.request.headers.contentLength <= 0) {
                    loggingUtil.logRequest(exchangeDecorator.request)
                }
            }
        }
    }
}

class LoggingRequestDecorator(val loggingUtil: LoggingUtil, val request: ServerHttpRequest) :
    ServerHttpRequestDecorator(request) {
    private var body: String? = null

    @Throws(IOException::class)
    override fun getBody(): Flux<DataBuffer> {
        return super.getBody().doOnNext { dataBuffer ->
            ByteArrayOutputStream().also { outputStream ->
                Channels.newChannel(outputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                body = StreamUtils.copyToString(outputStream, Charsets.UTF_8)
                outputStream.close()
            }
        }.doOnComplete {
            if (request.headers.contentLength > -1)
                loggingUtil.logRequest(request, body)
        }
    }
}

class LoggingResponseDecorator(val loggingUtil: LoggingUtil,private val response: ServerHttpResponse) : ServerHttpResponseDecorator(response) {
    private var bodyString: StringBuilder? = null

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        return super.writeWith(Flux.from(body).doOnNext { dataBuffer ->
            ByteArrayOutputStream().also { outputStream ->
                Channels.newChannel(outputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                bodyString = StringBuilder(StreamUtils.copyToString(outputStream, Charsets.UTF_8))
                outputStream.close()
            }
        }).doOnSuccess {
            loggingUtil.logResponse(response, bodyString?.toString())
        }.doOnError {
            loggingUtil.logResponse(response)
        }
    }

    override fun writeAndFlushWith(body: Publisher<out Publisher<out DataBuffer>>): Mono<Void> {
        return super.writeAndFlushWith(Flux.from(body).doOnNext {
            Flux.from(it)
                .doOnNext { dataBuffer ->
                    ByteArrayOutputStream().also { outputStream ->
                        Channels.newChannel(outputStream).write(dataBuffer.asByteBuffer().asReadOnlyBuffer())
                        bodyString = bodyString?.append(StreamUtils.copyToString(outputStream, Charsets.UTF_8))
                            ?: StringBuilder(StreamUtils.copyToString(outputStream, Charsets.UTF_8))
                        outputStream.close()
                    }
                }.subscribe()
        }.doOnComplete {
            loggingUtil.logResponse(response, bodyString?.toString())
        })
    }
}
