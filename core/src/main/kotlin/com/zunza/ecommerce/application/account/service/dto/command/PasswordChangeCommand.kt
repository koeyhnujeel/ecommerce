package com.zunza.ecommerce.application.account.service.dto.command

import com.zunza.ecommerce.domain.account.RegexPatterns


data class PasswordChangeCommand(
    val accountId: Long,
    val newPassword: String,
) {
    init {
        require(RegexPatterns.passwordPattern.matches(newPassword))
    }
}