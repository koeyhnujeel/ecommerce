package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.CustomerAddressCommandService
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultAddressCommand
import com.zunza.ecommerce.domain.customer.Customer
import com.zunza.ecommerce.domain.customer.CustomerNotFoundException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UpdateDefaultAddressUseCaseTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var updateDefaultAddressUseCase: UpdateDefaultAddressUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        updateDefaultAddressUseCase = CustomerAddressCommandService(customerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun updateDefaultAddress() {
        val accountId = 1L;
        val addressId = 1L
        val command = UpdateDefaultAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        updateDefaultAddressUseCase.updateDefaultAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.updateDefaultAddress(command.addressId)
        }
    }

    @Test
    fun updateDefaultAddressFailCustomerNotFound() {
        val accountId = 1L;
        val addressId = 1L
        val command = UpdateDefaultAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            updateDefaultAddressUseCase.updateDefaultAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.updateDefaultAddress(command.addressId)
        }
    }
}