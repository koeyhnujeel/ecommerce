package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.command.AccountRegisterCommand

interface RegisterCustomerAccountUseCase {
    fun registerCustomerAccount(registerCommand: AccountRegisterCommand): Long
}