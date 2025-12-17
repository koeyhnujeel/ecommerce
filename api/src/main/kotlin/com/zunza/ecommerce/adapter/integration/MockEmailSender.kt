package com.zunza.ecommerce.adapter.integration

import com.zunza.ecommerce.application.account.required.EmailSender
import org.springframework.stereotype.Component

@Component
class MockEmailSender : EmailSender {
    override fun send(email: String, title: String, message: String) {
        println("[Mock] EmailSender Sending email: $email")
    }
}