package com.zunza.ecommerce.unit

import com.zunza.ecommerce.domain.Customer
import com.zunza.ecommerce.dto.SignupCommand
import com.zunza.ecommerce.port.PasswordEncoder
import com.zunza.ecommerce.repository.CustomerRepository
import com.zunza.ecommerce.service.AuthService
import com.zunza.ecommerce.support.exception.BusinessException
import io.kotest.assertions.throwables.shouldNotThrow
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class AuthServiceUnitTests : FunSpec ({
    val customerRepository = mockk<CustomerRepository>()
    val passwordEncoder = mockk<PasswordEncoder>()
    val authService = AuthService(customerRepository, passwordEncoder)

    afterTest {
        clearAllMocks()
    }

    test("사용 가능한 이메일이면 예외를 던지지 않는다.") {
        val email = "new@email.com"
        every { customerRepository.existsByEmail(email) } returns false

        shouldNotThrow<BusinessException> {
            authService.validateEmailAvailable(email)
        }
    }

    test("이메일이 이미 존재하면 예외를 던진다.") {
        val email = "duplicate@email.com"
        every { customerRepository.existsByEmail(email) } returns true

        val exception = shouldThrow<BusinessException> {
            authService.validateEmailAvailable(email)
        }

        exception.errorCode.status shouldBe 409
        exception.message shouldBe "이미 사용 중인 이메일입니다."
    }

    test("사용 가능한 핸드폰 번호면 예외를 던지지 않는다.") {
        val phone = "010-1234-5678"
        every { customerRepository.existsByPhone(phone) } returns false

        shouldNotThrow<BusinessException> {
            authService.validatePhoneAvailable(phone)
        }
    }

    test("핸드폰 번호가 이미 존재하면 예외를 던진다.") {
        val phone = "010-1234-5678"
        every { customerRepository.existsByPhone(phone) } returns true

        val exception = shouldThrow<BusinessException> {
            authService.validatePhoneAvailable(phone)
        }

        exception.errorCode.status shouldBe 409
        exception.message shouldBe "이미 사용 중인 번호입니다."
    }

    test("회원가입 요청 시 고객이 생성되어 저장된다.") {
        val command = SignupCommand("new@example.com", "password1!", "홍길동", "010-1234-5678")
        val encodedPassword = "encoded_password"

        every { passwordEncoder.encode(command.password) } returns encodedPassword
        every { customerRepository.existsByNickname(any<String>()) } returns false
        every { customerRepository.save(any<Customer>()) } returns mockk<Customer>()

        authService.createCustomer(command)

        verify(exactly = 1) { passwordEncoder.encode(command.password) }
        verify(exactly = 1) { customerRepository.existsByNickname(any<String>()) }
        verify(exactly = 1) { customerRepository.save(any<Customer>()) }
    }
})
