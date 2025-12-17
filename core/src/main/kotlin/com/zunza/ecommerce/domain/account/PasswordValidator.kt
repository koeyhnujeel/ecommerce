package com.zunza.ecommerce.domain.account

object PasswordValidator {
    private val PASSWORD_PATTERN = Regex(
        "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$"
    )

    fun isValid(password: String): Boolean {
        return password.matches(PASSWORD_PATTERN)
    }
}