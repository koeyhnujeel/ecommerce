package com.zunza.ecommerce.application.account.service.dto.request

import jakarta.validation.constraints.Pattern

data class PasswordChangeRequest(
    @field:Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$")
    val newPassword: String,
)