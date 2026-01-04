package com.zunza.ecommerce.application.fixture

import com.zunza.ecommerce.application.partner.service.dto.command.RegisterPartnerCommand
import com.zunza.ecommerce.domain.partner.BusinessInfo
import com.zunza.ecommerce.domain.shared.BankAccount

object RegisterPartnerCommandFixture {

    fun createRegisterPartnerCommand(
        accountId: Long = 1L,
        partnerApplicationId: Long = 1L,
        businessNumber: String = "1234567890",
        companyName: String = "컴퍼니",
        bankName: String = "하나은행",
        accountNumber: String = "12345678911223",
        accountHolder: String = "홍길동"
    ) = RegisterPartnerCommand(
        accountId = accountId,
        partnerApplicationId = partnerApplicationId,
        businessInfo = BusinessInfo(
            businessNumber = businessNumber,
            companyName = companyName,
        ),
        settlementAccount = BankAccount(
            bankName = bankName,
            accountNumber = accountNumber,
            accountHolder = accountHolder,
        )
    )
}
