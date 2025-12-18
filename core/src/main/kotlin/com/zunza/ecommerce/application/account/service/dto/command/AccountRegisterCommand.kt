package com.zunza.ecommerce.application.account.service.dto.command


data class AccountRegisterCommand(
    val email: String,
    val password: String,
    val name: String,
    val phone: String,
) {
    init {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        val passwordRegex = Regex("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?]).{8,20}$")

        require(email.matches(emailRegex))
        require(password.matches(passwordRegex))
        require(name.length in 2..15)
        require(phone.length == 11)
    }
}