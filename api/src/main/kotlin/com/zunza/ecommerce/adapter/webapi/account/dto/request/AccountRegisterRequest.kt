package com.zunza.ecommerce.adapter.webapi.account.dto.request

import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand
import com.zunza.ecommerce.domain.account.RegexPatterns
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class AccountRegisterRequest(
    @field:Email
    val email: String,
    @field:Pattern(regexp = RegexPatterns.PASSWORD)
    val password: String,
    @field:Size(min = 2, max = 15)
    val name: String,
    @field:Size(min = 11, max = 11)
    val phone: String,
) {
    fun toCommand() = AccountRegisterCommand(
        email = email,
        password = password,
        name = name,
        phone = phone
    )
}