package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand

interface AccountRegister {
    fun registerCustomerAccount(registerCommand: AccountRegisterCommand): Long

    fun activateCustomerAccount(accountId: Long)

    fun deactivateCustomerAccount(accountId: Long)
}