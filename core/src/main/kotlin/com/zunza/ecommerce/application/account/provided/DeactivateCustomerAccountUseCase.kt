package com.zunza.ecommerce.application.account.provided

interface DeactivateCustomerAccountUseCase {
    fun deactivateCustomerAccount(accountId: Long)
}