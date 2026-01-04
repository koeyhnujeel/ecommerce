package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithShippingAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.CustomerCommandService
import com.zunza.ecommerce.application.customer.service.dto.command.DeleteShippingAddressCommand
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

class DeleteShippingAddressUseCaseTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var deleteShippingAddressUseCase: DeleteShippingAddressUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        deleteShippingAddressUseCase = CustomerCommandService(customerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun deleteAddress() {
        val accountId = 1L;
        val addressId = 1L
        val command = DeleteShippingAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        deleteShippingAddressUseCase.deleteShippingAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithShippingAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.deleteShippingAddress(command.addressId)
        }
    }

    @Test
    fun deleteAddressFailCustomerNotFound() {
        val accountId = 1L;
        val addressId = 1L
        val command = DeleteShippingAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithShippingAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            deleteShippingAddressUseCase.deleteShippingAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.deleteShippingAddress(command.addressId)
        }
    }
}