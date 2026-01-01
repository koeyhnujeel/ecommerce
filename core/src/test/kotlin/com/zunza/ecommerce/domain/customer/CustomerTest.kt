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
    fun registerShippingAddress() {
        customer.registerShippingAddress(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        )

        val shippingAddress = customer.shippingAddresses[0]

        shippingAddress.alias shouldBe "집"
        shippingAddress.receiverName shouldBe "홍길동"
        shippingAddress.address.roadAddress shouldBe "서울특별시 관악구 관악로 1"
        shippingAddress.address.detailAddress shouldBe ""
        shippingAddress.address.zipcode shouldBe "11111"
        shippingAddress.isDefault shouldBe true
    }

    @Test
    fun registerShippingAddressFailOver10() {
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
    fun updateShippingAddress() {
        val shippingAddress1 = ShippingAddress.create(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        ).setIdForTest(1L)

        val shippingAddress2 = ShippingAddress.create(
            alias = "학교",
            roadAddress = "서울특별시 관악구 관악로 123",
            detailAddress = "",
            receiverName = "심청이",
            zipcode = "11112",
            isDefault = false
        ).setIdForTest(2L)

        customer.shippingAddresses.add(shippingAddress1)
        customer.shippingAddresses.add(shippingAddress2)

        customer.updateShippingAddress(
            2L,
            alias = "직장",
            roadAddress = "서울특별시 관악구 관악로 4",
            detailAddress = "",
            receiverName = "이순신",
            zipcode = "22222",
            isDefault = false
        )

        val found = customer.shippingAddresses[1]

        found.alias shouldBe "직장"
        found.receiverName shouldBe "이순신"
        found.address.roadAddress shouldBe "서울특별시 관악구 관악로 4"
        found.address.detailAddress shouldBe ""
        found.address.zipcode shouldBe "22222"
        found.isDefault shouldBe false
    }

    @Test
    fun updateAddressFailShippingAddressNotFound() {
        shouldThrow<ShippingAddressNotFoundException> {
            customer.updateShippingAddress(
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
    fun deleteShippingAddress() {
        val shippingAddress1 = ShippingAddress.create(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = true
        ).setIdForTest(1L)

        val shippingAddress2 = ShippingAddress.create(
            alias = "학교",
            roadAddress = "서울특별시 관악구 관악로 123",
            detailAddress = "",
            receiverName = "심청이",
            zipcode = "11112",
            isDefault = false
        ).setIdForTest(2L)

        customer.shippingAddresses.add(shippingAddress1)
        customer.shippingAddresses.add(shippingAddress2)

        customer.shippingAddresses.size shouldBe 2

        customer.deleteShippingAddress(2L)

        customer.shippingAddresses.size shouldBe 1
    }

    @Test
    fun updateShippingDefaultAddress() {
        val shippingAddress1 = ShippingAddress.create(
            alias = "집",
            roadAddress = "서울특별시 관악구 관악로 1",
            detailAddress = "",
            receiverName = "홍길동",
            zipcode = "11111",
            isDefault = false
        ).setIdForTest(1L)

        customer.shippingAddresses.add(shippingAddress1)

        customer.shippingAddresses[0].isDefault shouldBe false

        customer.updateShippingDefaultAddress(1L)

        customer.shippingAddresses[0].isDefault shouldBe true
    }

    @Test
    fun updateShippingDefaultAddressFailAddressNotFound() {
        shouldThrow<ShippingAddressNotFoundException> {
            customer.updateShippingDefaultAddress(1L)
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
    customer.registerShippingAddress(
        alias = alias,
        roadAddress = roadAddress,
        detailAddress = detailAddress,
        receiverName = receiverName,
        zipcode = zipcode,
        isDefault = isDefault
    )
}