package com.zunza.ecommerce.application.account.service.dto.request

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AccountRegisterRequest(
    @field:Email
    val email: String,
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$")
    val password: String,
    @field:Size(min = 2, max = 15)
    val name: String,
    @field:Size(min = 11, max = 11)
    val phone: String,
)