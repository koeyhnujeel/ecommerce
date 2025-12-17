package com.zunza.ecommerce.application.account.provided

import com.zunza.ecommerce.application.account.service.dto.request.PasswordChangeRequest
import com.zunza.ecommerce.application.fixture.AccountRequestFixture
import com.zunza.ecommerce.config.TestConfiguration
import com.zunza.ecommerce.config.TestContainersConfiguration
import io.kotest.assertions.throwables.shouldThrow
import jakarta.validation.ConstraintViolationException
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.transaction.annotation.Transactional


@Transactional
@SpringBootTest(classes = [TestConfiguration::class, TestContainersConfiguration::class])
class AccountManagerTest(
    val accountManager: AccountManager,
    val accountRegister: AccountRegister,
) {
    @Test
    fun changePassword() {
        val response = accountRegister.registerCustomerAccount(AccountRequestFixture.createAccountRegisterRequest())

        accountRegister.activateCustomerAccount(response.accountId)

        val changeRequest = PasswordChangeRequest("verysecret1!")

        accountManager.changePassword(response.accountId, changeRequest)
    }

    @Test
    fun changePasswordRequestFail() {
        val changeRequest = PasswordChangeRequest("invalid")

        shouldThrow<ConstraintViolationException> { accountManager.changePassword(1L, changeRequest) }
    }
}