package com.zunza.ecommerce.application.account.required

interface EmailSender {
    fun send(email: String, title: String, message: String)
}