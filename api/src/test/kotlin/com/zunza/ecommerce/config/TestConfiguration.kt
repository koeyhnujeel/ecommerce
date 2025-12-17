package com.zunza.ecommerce.config

import com.zunza.ecommerce.application.account.required.EmailSender
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestConfiguration {
    @Bean
    fun emailSender(): EmailSender = object : EmailSender {
        override fun send(email: String, title: String, message: String) {
            println("[Test] EmailSender Sending email: $email")
        }
    }
}