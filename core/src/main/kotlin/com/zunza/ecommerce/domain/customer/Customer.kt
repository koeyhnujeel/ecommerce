package com.zunza.ecommerce.domain.customer

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class Customer private constructor(
    val accountId: Long,
    val name: String,
    val phone: String,
    val shippingAddresses: MutableList<ShippingAddress> = mutableListOf()
) : AbstractEntity<Customer>() {
    companion object {
        fun register(accountId: Long, name: String, phone: String, ): Customer {
            require(accountId > 0) { "accountId는 0 이하일 수 없습니다." }
            require(name.length >= 2) { "이름은 2자 이하일 수 없습니다." }
            require(phone.length == 11) { "전화번호는 11자리여야 합니다." }

            return Customer(
                accountId = accountId,
                name = name,
                phone = phone,
            )
        }
    }

    fun registerShippingAddress(
        alias: String,
        roadAddress: String,
        detailAddress: String,
        receiverName: String,
        zipcode: String,
        isDefault: Boolean
    ) {
        require(this.shippingAddresses.size < 10) { "주소는 최대 10개까지 등록 가능합니다." }

        val shouldBeDefault = shippingAddresses.isEmpty() || isDefault

        if (shouldBeDefault) {
            clearDefaultShippingAddress()
        }

        val shippingAddress = ShippingAddress.create(
            alias = alias,
            roadAddress = roadAddress,
            detailAddress = detailAddress,
            receiverName = receiverName,
            zipcode = zipcode,
            isDefault = shouldBeDefault
        )

        shippingAddresses.add(shippingAddress)
    }

    fun updateShippingAddress(
        addressId: Long,
        alias: String,
        roadAddress: String,
        detailAddress: String,
        receiverName: String,
        zipcode: String,
        isDefault: Boolean
    ) {
        val address = findAddressById(addressId)

        if (isDefault) clearDefaultShippingAddress()

        address.update(
            alias = alias,
            roadAddress = roadAddress,
            detailAddress = detailAddress,
            receiverName = receiverName,
            zipcode = zipcode,
            isDefault = isDefault
        )
    }

    fun deleteShippingAddress(addressId: Long) {
        this.shippingAddresses.removeIf { it.id == addressId }
    }

    fun updateShippingDefaultAddress(addressId: Long) {
        clearDefaultShippingAddress()

        findAddressById(addressId).markAsDefault()
    }

    private fun clearDefaultShippingAddress() {
        this.shippingAddresses.find { it.isDefault }
            ?.unmarkAsDefault()
    }

    private fun findAddressById(addressId: Long): ShippingAddress {
        return this.shippingAddresses.find { it.id == addressId }
            ?: throw ShippingAddressNotFoundException()
    }
}