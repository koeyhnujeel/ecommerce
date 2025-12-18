package com.zunza.ecommerce.adapter.webapi.account.dto.request

import com.zunza.ecommerce.application.account.service.dto.command.PasswordChangeCommand
import com.zunza.ecommerce.domain.account.RegexPatterns
import jakarta.validation.constraints.Pattern

data class PasswordChangeRequest(
    @field:Pattern(regexp = RegexPatterns.PASSWORD)
    val newPassword: String,
) {
    fun toCommand(accountId: Long) = PasswordChangeCommand(accountId, newPassword)
}