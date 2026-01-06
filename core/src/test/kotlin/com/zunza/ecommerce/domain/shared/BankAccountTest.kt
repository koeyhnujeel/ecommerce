package com.zunza.ecommerce.shared

import com.zunza.ecommerce.domain.shared.BankAccount
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class BankAccountTest {
    @Test
    fun backAccount() {
        val bankAccount = BankAccount(
            "하나은행",
            "11111111111111",
            "홍길동"
        )

        bankAccount.bankName shouldBe "하나은행"
        bankAccount.accountNumber shouldBe "11111111111111"
        bankAccount.accountHolder shouldBe "홍길동"
    }

    @Test
    fun bankAccountFail() {
        shouldThrow<IllegalArgumentException> {
            BankAccount("", "11111111111111", "홍길동")
        }.message shouldBe "은행은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            BankAccount("하나은행", "", "홍길동")
        }.message shouldBe "계좌번호는 필수입니다."

        shouldThrow<IllegalArgumentException> {
            BankAccount("하나은행", "11111111111111", "")
        }.message shouldBe "예금주명은 필수입니다."
    }
}