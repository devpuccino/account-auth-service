package com.devpuccino.account.authservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class AccountAuthServiceApplication

fun main(args: Array<String>) {
    runApplication<AccountAuthServiceApplication>(*args)
}