package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.command.PasswordChangeCommand

interface AccountManager {
    fun activateCustomerAccount(accountId: Long)

    fun deactivateCustomerAccount(accountId: Long)

    fun changePassword(changeCommand: PasswordChangeCommand)

    fun grantPartnerRole(accountId: Long)
}