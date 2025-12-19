package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.service.CustomerRegisterService
import com.zunza.ecommerce.application.customer.service.dto.command.CustomerRegisterCommand
import com.zunza.ecommerce.domain.customer.Customer
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith


@ExtendWith(MockKExtension::class)
class CustomerRegisterTest{
    @MockK
    lateinit var customerRepository: CustomerRepository

    @InjectMockKs
    lateinit var customerRegister: CustomerRegisterService

    @Test
    fun register() {
        val registerCommand = CustomerRegisterCommand(1L, "홍길동", "01012345678")

        val customer = mockk<Customer>()

        mockkObject(Customer.Companion)

        every { Customer.register(any(), any(), any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        customerRegister.register(registerCommand)

        verify(exactly = 1) {
            Customer.register(registerCommand.accountId, registerCommand.name, registerCommand.phone)
            customerRepository.save(customer)
        }
    }

    @Test
    fun customerRegisterCommandFail() {
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(0L, "홍길동","01012345678") }
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(1L, "홍","01012345678") }
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(1L, "홍길동","1012345678") }
    }
}