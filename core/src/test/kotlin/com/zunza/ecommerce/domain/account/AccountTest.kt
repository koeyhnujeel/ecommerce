package com.zunza.ecommerce.domain.account

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AccountTest {
    val passwordEncoder = AccountFixture.createPasswordEncoder()
    lateinit var account: Account

    @BeforeEach
    fun setUp() {
        account = AccountFixture.createRegisteredAccount()
    }

    @Test
    fun register() {
        val account = Account.register("zunza@email.com", "password1!", passwordEncoder)

        account.email.address shouldBe "zunza@email.com"
        account.passwordHash shouldBe passwordEncoder.encode("password1!")
        account.status shouldBe AccountStatus.PENDING
        account.role shouldBe UserRole.ROLE_CUSTOMER
        account.registeredAt shouldNotBe null
        account.activatedAt shouldBe null
        account.lastLoginAt shouldBe null
        account.deactivatedAt shouldBe null
    }

    @Test
    fun registerFailInvalidEmail() {
        shouldThrow<IllegalArgumentException> {
            Account.register("zunzaemail.com", "password1!", passwordEncoder)
        }

        shouldThrow<IllegalArgumentException> {
            Account.register("zunza@emailcom", "password1!", passwordEncoder)
        }

        shouldThrow<IllegalArgumentException> {
            Account.register("zunzaemailcom", "password1!", passwordEncoder)
        }
    }

    @Test
    fun registerFailInvalidPassword() {
        shouldThrow<IllegalArgumentException> {
            Account.register("zunza@email.com", "short", passwordEncoder)
        }

        shouldThrow<IllegalArgumentException> {
            Account.register("zunza@email.com", "long!1longlonglonglonglong", passwordEncoder)
        }

        shouldThrow<IllegalArgumentException> {
            Account.register("zunza@email.com", "password1", passwordEncoder)
        }

        shouldThrow<IllegalArgumentException> {
            Account.register("zunza@email.com", "password!", passwordEncoder)
        }
    }

    @Test
    fun activate() {
        account.activate()

        account.status shouldBe AccountStatus.ACTIVE
        account.activatedAt shouldNotBe null
    }

    @Test
    fun activateFail() {
        account.activate()

        shouldThrow<IllegalStateException> { account.activate() }
    }

    @Test
    fun deactivate() {
        account.activate()

        account.deactivate()

        account.status shouldBe AccountStatus.DEACTIVATED
        account.deactivatedAt shouldNotBe null
    }

    @Test
    fun deactivateFail() {
        account.activate()

        account.deactivate()

        shouldThrow<IllegalStateException> { account.deactivate() }
    }

    @Test
    fun changePassword() {
        account.activate()

        account.changePassword("newPassword1!", passwordEncoder)

        account.passwordHash shouldBe passwordEncoder.encode("newPassword1!")
    }

    @Test
    fun changePasswordFailNotActive() {
        shouldThrow<IllegalStateException> { account.changePassword("newPassword1!", passwordEncoder) }
    }

    @Test
    fun changePasswordFailInvalidPassword() {
        account.activate()

        shouldThrow<IllegalArgumentException> { account.changePassword("newPassword1", passwordEncoder) }
    }

    @Test
    fun login() {
        account.activate()

        account.lastLoginAt shouldBe null

        account.login("password1!", passwordEncoder)

        account.lastLoginAt shouldNotBe null
    }

    @Test
    fun loginFailDeactivateAccount() {
        shouldThrow<IllegalStateException> { account.login("password1!", passwordEncoder) }

        account.lastLoginAt shouldBe null
    }

    @Test
    fun loginFailInvalidPassword() {
        account.activate()

        shouldThrow<InvalidCredentialsException> { account.login("invalid1!", passwordEncoder) }

        account.lastLoginAt shouldBe null
    }
}