package com.zunza.api.customer.unit

import com.zunza.customer.api.domain.customer.dto.request.SignupRequestDto
import com.zunza.customer.api.domain.customer.service.CustomerService
import com.zunza.domain.entity.Customer
import com.zunza.domain.enums.UserRole
import com.zunza.domain.repository.CustomerRepository
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder

class CustomerServiceTests :
    FunSpec({
        val customerRepository = mockk<CustomerRepository>()
        val passwordEncoder = mockk<PasswordEncoder>()
        val customerService = CustomerService(customerRepository, passwordEncoder)

        test("signup") {
            val request = SignupRequestDto("xxx@example.com", "password1!", "김블루", "010-1234-5678")
            val encodedPassword = "encoded password"
            val customerSlot = slot<Customer>()

            every { passwordEncoder.encode(request.password) } returns encodedPassword
            every { customerRepository.existsByNickname(any<String>()) } returns false
            every { customerRepository.save(capture(customerSlot)) } returns mockk()

            customerService.signup(request)

            verify(exactly = 1) { customerRepository.save(any<Customer>()) }

            val captured = customerSlot.captured
            captured.email shouldBe "xxx@example.com"
            captured.password shouldBe encodedPassword
            captured.name shouldBe "김블루"
            captured.nickname.shouldNotBeNull()
            captured.phone shouldBe "010-1234-5678"
            captured.userRole shouldBe UserRole.ROLE_CUSTOMER
            captured.point shouldBe 0L
        }
    })
