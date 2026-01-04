package com.zunza.ecommerce.shared

import com.zunza.ecommerce.domain.shared.Email
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test

class EmailTest {
    @Test
    fun email() {
        Email("zunza@email.com").address shouldBe "zunza@email.com"
        Email("example@email.com").address shouldBe "example@email.com"
        Email("zunza123@email.com").address shouldBe "zunza123@email.com"
    }

    @Test
    fun emailFail() {
        shouldThrow<IllegalArgumentException> { Email("") }.message shouldBe "이메일은 필수입니다."
        shouldThrow<IllegalArgumentException> { Email(" ") }.message shouldBe "이메일은 필수입니다."
        shouldThrow<IllegalArgumentException> { Email("gildong") }.message shouldBe "잘못된 이메일 형식입니다."
        shouldThrow<IllegalArgumentException> { Email("zunza.com") }.message shouldBe "잘못된 이메일 형식입니다."
        shouldThrow<IllegalArgumentException> { Email("zunza!.com") }.message shouldBe "잘못된 이메일 형식입니다."
        shouldThrow<IllegalArgumentException> { Email("zunza!@.com") }.message shouldBe "잘못된 이메일 형식입니다."
    }
}