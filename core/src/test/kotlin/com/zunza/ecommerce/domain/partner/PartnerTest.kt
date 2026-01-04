package com.zunza.ecommerce.domain.partner

import com.zunza.ecommerce.domain.shared.BankAccount
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class PartnerTest {
    @Test
    fun register() {
        val partner = Partner.register(
            1L,
            22L,
            BusinessInfo("1234567899", "컴퍼니"),
            BankAccount("하나은행", "11111111111111", "홍길동")
        )

        partner.accountId shouldBe 1L
        partner.partnerApplicationId shouldBe 22L
        partner.businessInfo.businessNumber shouldBe "1234567899"
        partner.businessInfo.companyName shouldBe "컴퍼니"
        partner.settlementAccount.bankName shouldBe "하나은행"
        partner.settlementAccount.accountNumber shouldBe "11111111111111"
        partner.settlementAccount.accountHolder shouldBe "홍길동"
    }
}