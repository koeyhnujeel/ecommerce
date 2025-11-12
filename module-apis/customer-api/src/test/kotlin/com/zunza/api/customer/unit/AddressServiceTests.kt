package com.zunza.api.customer.unit

import com.zunza.common.support.exception.BusinessException
import com.zunza.customer.api.domain.customer.dto.request.AddressRegisterRequestDto
import com.zunza.customer.api.domain.customer.service.AddressService
import com.zunza.domain.entity.Customer
import com.zunza.domain.entity.CustomerAddress
import com.zunza.domain.repository.CustomerAddressRepository
import com.zunza.domain.repository.CustomerRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.http.HttpStatus
import java.util.Optional

class AddressServiceTests :
    FunSpec({
        val customerRepository = mockk<CustomerRepository>()
        val customerAddressRepository = mockk<CustomerAddressRepository>()
        val addressService = AddressService(customerRepository, customerAddressRepository)

        beforeTest {
            clearAllMocks()
        }

        val request =
            AddressRegisterRequestDto(
                "집",
                "03000",
                "대전광역시 중구 무슨동 111-1",
                "1동 101호",
                true,
            )

        test("배송지 등록 성공: 유효한 사용자가 주소 등록을 요청하면 성공적으로 저장된다.") {
            val customerId = 1L
            val customer =
                Customer(
                    customerId,
                    "xxx@example.com",
                    "password1!",
                    "홍길동",
                    "따뜻한 호랑이 1",
                    "010-1234-5678",
                )

            every { customerRepository.findById(any()) } returns Optional.of(customer)
            every { customerAddressRepository.save(any()) } returns mockk<CustomerAddress>()

            addressService.registerAddress(customerId, request)

            verify(exactly = 1) { customerRepository.findById(customerId) }
            verify(exactly = 1) { customerAddressRepository.save(any()) }
        }

        test("배송지 등록 실패: 유효하지 않은 사용자가 주소 등록을 요청하면 404 예외가 발생한다.") {
            val customerId = 2L

            every { customerRepository.findById(any()) } returns Optional.empty()

            val exception =
                shouldThrow<BusinessException> {
                    addressService.registerAddress(customerId, request)
                }

            exception.message shouldBe "고객 정보를 찾을 수 없습니다."
            exception.errorCode.httpStatus shouldBe HttpStatus.NOT_FOUND

            verify(exactly = 1) { customerRepository.findById(customerId) }
            verify(exactly = 0) { customerAddressRepository.save(any()) }
        }
    })
