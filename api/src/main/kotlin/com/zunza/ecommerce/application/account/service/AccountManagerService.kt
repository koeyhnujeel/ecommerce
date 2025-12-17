package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.provided.AccountManager
import com.zunza.ecommerce.application.account.required.AccountRepository
import com.zunza.ecommerce.application.account.required.findByIdOrThrow
import com.zunza.ecommerce.application.account.service.dto.request.PasswordChangeRequest
import com.zunza.ecommerce.application.account.service.dto.response.PasswordChangeResponse
import com.zunza.ecommerce.domain.account.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.annotation.Validated

@Service
@Validated
@Transactional
class AccountManagerService(
    private val passwordEncoder: PasswordEncoder,
    private val accountRepository: AccountRepository,
) : AccountManager {
    override fun changePassword(
        accountId: Long,
        changeRequest: PasswordChangeRequest
    ): PasswordChangeResponse {
        val account = accountRepository.findByIdOrThrow(accountId)

        account.changePassword(changeRequest.newPassword, passwordEncoder)

        accountRepository.save(account)

        return PasswordChangeResponse.from(account)
    }
}