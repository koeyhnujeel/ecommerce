package com.zunza.ecommerce.domain.account

object RegexPatterns {
    const val EMAIL = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    const val PASSWORD = "^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$"

    val passwordPattern = PASSWORD.toRegex()
    val emailPattern = EMAIL.toRegex()
}