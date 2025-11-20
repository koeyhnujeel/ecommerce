package com.zunza.ecommerce.dto

data class SignupCommand(
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
)
