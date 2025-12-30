package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.service.CustomerCommandService
import com.zunza.ecommerce.application.customer.service.dto.command.CustomerRegisterCommand
import com.zunza.ecommerce.domain.customer.Customer
import io.kotest.assertions.throwables.shouldThrow
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RegisterCustomerUseCaseTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var registerCustomerUseCase: RegisterCustomerUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        registerCustomerUseCase = CustomerCommandService(customerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun registerCustomer() {
        val registerCommand = CustomerRegisterCommand(1L, "홍길동", "01012345678")

        val customer = mockk<Customer>()

        mockkObject(Customer.Companion)

        every { Customer.register(any(), any(), any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        registerCustomerUseCase.registerCustomer(registerCommand)

        verify(exactly = 1) {
            Customer.register(registerCommand.accountId, registerCommand.name, registerCommand.phone)
            customerRepository.save(customer)
        }
    }

    @Test
    fun customerRegisterCustomerCommandFail() {
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(0L, "홍길동","01012345678") }
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(1L, "홍","01012345678") }
        shouldThrow<IllegalArgumentException> { CustomerRegisterCommand(1L, "홍길동","1012345678") }
    }
}