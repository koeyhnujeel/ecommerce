package com.zunza.ecommerce.domain.seller

import com.zunza.ecommerce.domain.shared.BankAccount
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class SellerTest {
    @Test
    fun register() {
        val seller = Seller.register(
            1L,
            22L,
            BusinessInfo("1234567899", "컴퍼니"),
            BankAccount("하나은행", "11111111111111", "홍길동")
        )

        seller.accountId shouldBe 1L
        seller.sellerApplicationId shouldBe 22L
        seller.businessInfo.businessNumber shouldBe "1234567899"
        seller.businessInfo.companyName shouldBe "컴퍼니"
        seller.settlementAccount.bankName shouldBe "하나은행"
        seller.settlementAccount.accountNumber shouldBe "11111111111111"
        seller.settlementAccount.accountHolder shouldBe "홍길동"
    }
}