package com.zunza.ecommerce.application.auth.service.dto.command

data class LoginCommand(
    val email: String,
    val password: String
)
