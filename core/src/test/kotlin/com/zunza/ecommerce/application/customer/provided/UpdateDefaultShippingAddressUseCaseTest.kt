package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.CustomerCommandService
import com.zunza.ecommerce.application.customer.service.dto.command.UpdateDefaultShippingAddressCommand
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

class UpdateDefaultShippingAddressUseCaseTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var updateDefaultShippingAddressUseCase: UpdateDefaultShippingAddressUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        updateDefaultShippingAddressUseCase = CustomerCommandService(customerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun updateDefaultAddress() {
        val accountId = 1L;
        val addressId = 1L
        val command = UpdateDefaultShippingAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        updateDefaultShippingAddressUseCase.updateDefaultShippingAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithShippingAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.updateShippingDefaultAddress(command.addressId)
        }
    }

    @Test
    fun updateDefaultAddressFailCustomerNotFound() {
        val accountId = 1L;
        val addressId = 1L
        val command = UpdateDefaultShippingAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            updateDefaultShippingAddressUseCase.updateDefaultShippingAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.updateShippingDefaultAddress(command.addressId)
        }
    }
}