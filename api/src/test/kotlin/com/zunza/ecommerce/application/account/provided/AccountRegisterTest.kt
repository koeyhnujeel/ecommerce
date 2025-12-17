package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.request.AccountRegisterRequest
import com.zunza.ecommerce.application.fixture.AccountRequestFixture
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import com.zunza.ecommerce.domain.account.AccountNotFoundException
import com.zunza.ecommerce.domain.account.DuplicateEmailException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional


@Transactional
@SpringBootTest(classes = [TestContainersConfiguration::class, TestConfiguration::class])
class AccountRegisterTest(
    val accountRegister: AccountRegister,
) {
    lateinit var registerRequest: AccountRegisterRequest

    @BeforeEach
    fun setup() {
        registerRequest = AccountRequestFixture.createAccountRegisterRequest()
    }

    @Test
    fun register() {
        val registerResponse = accountRegister.registerCustomerAccount(registerRequest)

        registerResponse.accountId shouldNotBe null
        registerResponse.email shouldBe registerRequest.email
        registerResponse.registeredAt shouldNotBe null
    }

    @Test
    fun registerFailDuplicateEmail() {
        accountRegister.registerCustomerAccount(registerRequest)

        shouldThrow<DuplicateEmailException> { accountRegister.registerCustomerAccount(registerRequest) }
    }

    @Test
    fun customerRegisterRequestFail() {
        shouldThrow<ConstraintViolationException> {
            accountRegister.registerCustomerAccount(AccountRequestFixture.createAccountRegisterRequest(email = "zunza.com"))
        }

        shouldThrow<ConstraintViolationException> {
            accountRegister.registerCustomerAccount(AccountRequestFixture.createAccountRegisterRequest(password = "invalid"))
        }

        shouldThrow<ConstraintViolationException> {
            accountRegister.registerCustomerAccount(AccountRequestFixture.createAccountRegisterRequest(name = "이"))
        }

        shouldThrow<ConstraintViolationException> {
            accountRegister.registerCustomerAccount(AccountRequestFixture.createAccountRegisterRequest(phone = "1012345678"))
        }
    }

    @Test
    fun activate() {
        val registerResponse = accountRegister.registerCustomerAccount(registerRequest)

        val activateResponse = accountRegister.activateCustomerAccount(registerResponse.accountId)

        activateResponse.accountId shouldBe registerResponse.accountId
        activateResponse.email shouldBe registerResponse.email
        activateResponse.activatedAt shouldNotBe null
    }

    @Test
    fun activateFailAccountNotFound() {
        shouldThrow<AccountNotFoundException> { accountRegister.activateCustomerAccount(1_000L) }
    }

    @Test
    fun deactivate() {
        val registerResponse = accountRegister.registerCustomerAccount(registerRequest)

        val activateResponse = accountRegister.activateCustomerAccount(registerResponse.accountId)

        val deactivateResponse = accountRegister.deactivateCustomerAccount(activateResponse.accountId)

        deactivateResponse.accountId shouldBe activateResponse.accountId
        deactivateResponse.email shouldBe activateResponse.email
        deactivateResponse.deactivatedAt shouldNotBe null
    }

    @Test
    fun deactivateFailAccountNotFound() {
        shouldThrow<AccountNotFoundException> { accountRegister.deactivateCustomerAccount(1_000L) }
    }
}