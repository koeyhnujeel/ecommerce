package com.zunza.ecommerce.domain.customer

import com.zunza.ecommerce.domain.AbstractEntity
import jakarta.persistence.Entity

@Entity
class Customer private constructor(
    val accountId: Long,
    val name: String,
    val phone: String,
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
}