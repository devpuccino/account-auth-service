package com.devpuccino.account.authservice.controller

import com.devpuccino.account.authservice.contant.AuthResponseStatus
import com.devpuccino.account.authservice.contant.Namespace
import com.devpuccino.account.authservice.dto.response.CommonResponse
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/v1/authentication")
class AuthenticationController {
    @PostMapping("/login")
    fun login(@RequestBody request:Map<String,String>):Mono<CommonResponse<Any?>>{
        return Mono.just<CommonResponse<Any?>?>(CommonResponse(code=AuthResponseStatus.SUCCESS.code, message = AuthResponseStatus.SUCCESS.message, namespace = Namespace.ACCOUNT_AUTH_SERVICE_NAMESPACE)).doOnSubscribe {
            println(">>>>")
        }
    }
}