package com.zunza.ecommerce.domain.customer

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class Address(
    var alias: String,
    var roadAddress: String,
    var detailAddress: String,
    var receiverName: String,
    var zipcode: String,
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
        ): Address {
            require(alias.isNotBlank()) { "배송지 별칭은 필수입니다." }
            require(roadAddress.isNotBlank()) { "도로명 주소는 필수입니다." }
            require(receiverName.isNotBlank()) { "수령인 이름은 필수입니다." }
            require(zipcode.matches(Regex("\\d{5}"))) { "우편번호 형식이 올바르지 않습니다." }

            return Address(
                alias = alias,
                roadAddress = roadAddress,
                detailAddress = detailAddress,
                receiverName = receiverName,
                zipcode = zipcode,
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
        this.roadAddress = roadAddress
        this.detailAddress = detailAddress
        this.receiverName = receiverName
        this.zipcode = zipcode
        this.isDefault = isDefault
    }

    fun unmarkAsDefault() {
        this.isDefault = false
    }

    fun markAsDefault() {
        this.isDefault = true
    }
}