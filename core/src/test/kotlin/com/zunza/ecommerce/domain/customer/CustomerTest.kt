package com.zunza.ecommerce.domain.customer

import com.zunza.ecommerce.domain.AbstractEntity
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

fun <T : AbstractEntity> T.setIdForTest(id: Long): T {
    val property = AbstractEntity::class.memberProperties
        .find { it.name == "id" }

    property?.let {
        it.isAccessible = true
        val field = AbstractEntity::class.java.getDeclaredField("id")
        field.isAccessible = true
        field.set(this, id)
    }
    return this
}

class CustomerTest {
    lateinit var customer: Customer

    @BeforeEach
    fun setUp() {
        customer = CustomerFixture.createCustomer()
    }

    @Test
    fun register() {
        val customer = Customer.register(1L, name = "홍길동", phone = "01012345678")

        customer.accountId shouldBe 1L
        customer.name shouldBe "홍길동"
        customer.phone shouldBe "01012345678"
    }

    @Test
    fun registerFail() {
        shouldThrow<IllegalArgumentException> { Customer.register(0L, name = "홍길동", phone = "01012345678") }
        shouldThrow<IllegalArgumentException> { Customer.register(1L, name = "홍", phone = "01012345678") }
        shouldThrow<IllegalArgumentException> { Customer.register(1L, name = "홍길동", phone = "0101234567") }
    }

    @Test
    fun registerAddress() {
        customer.registerAddress(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        )

        val address = customer.addresses[0]

        address.alias shouldBe "집"
        address.roadAddress shouldBe "서울특별시 관악구 관악로 1"
        address.detailAddress shouldBe ""
        address.receiverName shouldBe "홍길동"
        address.zipcode shouldBe "11111"
        address.isDefault shouldBe true
    }

    @Test
    fun registerAddressFailOver10() {
        repeat(10) {
            registerAddress(customer)
        }

        shouldThrow<IllegalArgumentException> {
            registerAddress(customer)
        }.message shouldBe "주소는 최대 10개까지 등록 가능합니다."
    }

    @Test
    fun registerFailInvalidArgument() {
        shouldThrow<IllegalArgumentException> {
            registerAddress(customer, alias = "")
        }.message shouldBe "배송지 별칭은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            registerAddress(customer, roadAddress = "")
        }.message shouldBe "도로명 주소는 필수입니다."

        shouldThrow<IllegalArgumentException> {
            registerAddress(customer, receiverName = "")
        }.message shouldBe "수령인 이름은 필수입니다."

        shouldThrow<IllegalArgumentException> {
            registerAddress(customer, zipcode = "1111")
        }.message shouldBe "우편번호 형식이 올바르지 않습니다."
    }

    @Test
    fun updateAddress() {
        val address1 = Address.create(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        ).setIdForTest(1L)

        val address2 = Address.create(
            alias = "학교",
            roadAddress = "서울특별시 관악구 관악로 123",
            detailAddress = "",
            receiverName = "심청이",
            zipcode = "11112",
            isDefault = false
        ).setIdForTest(2L)

        customer.addresses.add(address1)
        customer.addresses.add(address2)

        customer.updateAddress(
            2L,
            alias = "직장",
            roadAddress = "서울특별시 관악구 관악로 4",
            detailAddress = "",
            receiverName = "이순신",
            zipcode = "22222",
            isDefault = false
        )

        val found = customer.addresses[1]

        found.alias shouldBe "직장"
        found.roadAddress shouldBe "서울특별시 관악구 관악로 4"
        found.detailAddress shouldBe ""
        found.receiverName shouldBe "이순신"
        found.zipcode shouldBe "22222"
        found.isDefault shouldBe false
    }

    @Test
    fun updateAddressFailAddressNotFound() {
        shouldThrow<AddressNotFoundException> {
            customer.updateAddress(
                1L,
                alias = "직장",
                roadAddress = "서울특별시 관악구 관악로 4",
                detailAddress = "",
                receiverName = "이순신",
                zipcode = "22222",
                isDefault = true
            )
        }.message shouldBe "등록되지 않은 주소입니다."
    }

    @Test
    fun deleteAddress() {
        val address1 = Address.create(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        ).setIdForTest(1L)

        val address2 = Address.create(
            alias = "학교",
            roadAddress = "서울특별시 관악구 관악로 123",
            detailAddress = "",
            receiverName = "심청이",
            zipcode = "11112",
            isDefault = false
        ).setIdForTest(2L)

        customer.addresses.add(address1)
        customer.addresses.add(address2)

        customer.addresses.size shouldBe 2

        customer.deleteAddress(2L)

        customer.addresses.size shouldBe 1
    }

    @Test
    fun updateDefaultAddress() {
        val address1 = Address.create(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = false
        ).setIdForTest(1L)

        customer.addresses.add(address1)

        customer.addresses[0].isDefault shouldBe false

        customer.updateDefaultAddress(1L)

        customer.addresses[0].isDefault shouldBe true
    }

    @Test
    fun updateDefaultAddressFailAddressNotFound() {
        shouldThrow<AddressNotFoundException> {
            customer.updateDefaultAddress(1L)
        }.message shouldBe "등록되지 않은 주소입니다."
    }
}

private fun registerAddress(
    customer: Customer,
    alias: String = "집",
    roadAddress: String = "서울특별시 관악구 관악로 1",
    detailAddress: String = "",
    receiverName: String = "홍길동",
    zipcode: String = "11111",
    isDefault: Boolean = true
) {
    customer.registerAddress(
        alias = alias,
        roadAddress = roadAddress,
        detailAddress = detailAddress,
        receiverName = receiverName,
        zipcode = zipcode,
        isDefault = isDefault
    )
}