package com.zunza.ecommerce.dto.command

data class SignupCommand(
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
)
