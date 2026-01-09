package com.zunza.ecommerce.domain.customer

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.shared.Address
import jakarta.persistence.Entity

@Entity
class ShippingAddress(
    var alias: String,
    var receiverName: String,
    var address: Address,
    var isDefault: Boolean
) : AbstractEntity() {
    companion object {
        fun create(
            alias: String,
            roadAddress: String,
            detailAddress: String,
            receiverName: String,
            zipcode: String,
            isDefault: Boolean
        ): ShippingAddress {
            require(alias.isNotBlank()) { "배송지 별칭은 필수입니다." }
            require(receiverName.isNotBlank()) { "수령인 이름은 필수입니다." }

            return ShippingAddress(
                alias = alias,
                receiverName = receiverName,
                address = Address(roadAddress, detailAddress, zipcode),
                isDefault = isDefault
            )
        }
    }

    fun update(
        alias: String,
        roadAddress: String,
        detailAddress: String,
        receiverName: String,
        zipcode: String,
        isDefault: Boolean
    ) {
        this.alias = alias
        this.receiverName = receiverName
        this.address = Address(roadAddress, detailAddress, zipcode)
        this.isDefault = isDefault
    }

    fun unmarkAsDefault() {
        this.isDefault = false
    }

    fun markAsDefault() {
        this.isDefault = true
    }
}