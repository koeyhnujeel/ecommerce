package com.zunza.ecommerce.application.customer.provided

import com.zunza.ecommerce.application.customer.required.CustomerRepository
import com.zunza.ecommerce.application.customer.required.findWithAddressesOrThrow
import com.zunza.ecommerce.application.customer.service.CustomerAddressCommandService
import com.zunza.ecommerce.application.customer.service.dto.command.DeleteAddressCommand
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

class DeleteAddressUseCaseTest {
    lateinit var customerRepository: CustomerRepository
    lateinit var deleteAddressUseCase: DeleteAddressUseCase

    @BeforeEach
    fun setUp() {
        customerRepository = mockk()
        deleteAddressUseCase = CustomerAddressCommandService(customerRepository)
    }

    @AfterEach
    fun cleanUp() {
        clearAllMocks()
    }

    @Test
    fun deleteAddress() {
        val accountId = 1L;
        val addressId = 1L
        val command = DeleteAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } returns customer
        every { customerRepository.save(any()) } returns customer

        deleteAddressUseCase.deleteAddress(command)

        verify(exactly = 1) {
            customerRepository.findWithAddressesOrThrow(accountId)
            customerRepository.save(customer)
            customer.deleteAddress(command.addressId)
        }
    }

    @Test
    fun deleteAddressFailCustomerNotFound() {
        val accountId = 1L;
        val addressId = 1L
        val command = DeleteAddressCommand(accountId, addressId)

        val customer = mockk<Customer>(relaxed = true) {
            every { id } returns accountId
        }

        every { customerRepository.findWithAddressesOrThrow(any()) } throws CustomerNotFoundException()

        shouldThrow<CustomerNotFoundException> {
            deleteAddressUseCase.deleteAddress(command)
        }.message shouldBe "존재하지 않는 회원입니다."

        verify(exactly = 0) {
            customerRepository.save(customer)
            customer.deleteAddress(command.addressId)
        }
    }
}