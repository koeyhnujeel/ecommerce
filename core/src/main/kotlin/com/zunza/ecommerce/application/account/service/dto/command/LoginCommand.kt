package com.zunza.ecommerce.application.account.service.dto.command

data class LoginCommand(
    val email: String,
    val password: String
)
