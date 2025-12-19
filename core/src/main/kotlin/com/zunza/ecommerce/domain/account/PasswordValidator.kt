package com.zunza.ecommerce.domain.account

object PasswordValidator {
    fun isValid(password: String): Boolean {
        return RegexPatterns.passwordPattern.matches(password)
    }
}