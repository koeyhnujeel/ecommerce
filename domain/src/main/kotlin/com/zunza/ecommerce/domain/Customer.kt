package com.zunza.ecommerce.domain

import com.zunza.ecommerce.domain.enums.UserType

data class Customer(
    val id: Long = 0,
    val name: String,
    val nickname: String,
    val phone: String,
    var point: Long,
    val user: User,
) {
    val email: String get() = this.user.email
    val password: String get() = this.user.password
    val userType: UserType get() = this.user.userType

    companion object {
        fun of(
            email: String,
            password: String,
            name: String,
            nickname: String,
            phone: String,
        ): Customer {
            val user = User.of(email, password, UserType.CUSTOMER)

            return Customer(
                name = name,
                nickname = nickname,
                phone = phone,
                point = 0L,
                user = user
            )
        }
    }
}
