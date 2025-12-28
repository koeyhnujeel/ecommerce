package com.zunza.ecommerce.domain.customer

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany

@Entity
class Customer private constructor(
    val accountId: Long,
    val name: String,
    val phone: String,
    @OneToMany(
        mappedBy = "customer",
        cascade = [CascadeType.ALL],
        orphanRemoval = true
    )
    val addresses: MutableList<Address> = mutableListOf()
) : AbstractEntity() {
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

    fun registerAddress(
        alias: String,
        roadAddress: String,
        detailAddress: String,
        receiverName: String,
        zipcode: String,
        isDefault: Boolean
    ) {
        require(this.addresses.size < 10) { "주소는 최대 10개까지 등록 가능합니다." }

        val shouldBeDefault = addresses.isEmpty() || isDefault

        if (shouldBeDefault) {
            clearDefaultAddress()
        }

        val address = Address.create(
            alias = alias,
            roadAddress = roadAddress,
            detailAddress = detailAddress,
            receiverName = receiverName,
            zipcode = zipcode,
            isDefault = shouldBeDefault
        )

        addresses.add(address)
    }

    fun updateAddress(
        addressId: Long,
        alias: String,
        roadAddress: String,
        detailAddress: String,
        receiverName: String,
        zipcode: String,
        isDefault: Boolean
    ) {
        val address = findAddressById(addressId)

        if (isDefault) clearDefaultAddress()

        address.update(
            alias = alias,
            roadAddress = roadAddress,
            detailAddress = detailAddress,
            receiverName = receiverName,
            zipcode = zipcode,
            isDefault = isDefault
        )
    }

    fun deleteAddress(addressId: Long) {
        this.addresses.removeIf { it.id == addressId }
    }

    fun updateDefaultAddress(addressId: Long) {
        clearDefaultAddress()

        findAddressById(addressId).markAsDefault()
    }

    private fun clearDefaultAddress() {
        this.addresses.find { it.isDefault }
            ?.unmarkAsDefault()
    }

    private fun findAddressById(addressId: Long): Address {
        return this.addresses.find { it.id == addressId }
            ?: throw AddressNotFoundException()
    }
}