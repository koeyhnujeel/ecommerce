package com.zunza.domain.entity

import com.zunza.domain.enums.UserRole
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "customers")
class Customer(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val email: String = "",
    @Column(nullable = false)
    var password: String = "",
    @Column(nullable = false)
    val name: String = "",
    @Column(nullable = false, unique = true)
    var nickname: String = "",
    @Column(nullable = false, unique = true)
    var phone: String = "",
    @Column(nullable = false)
    var point: Long = 0L,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val userRole: UserRole = UserRole.ROLE_CUSTOMER,
) : BaseTimeEntity() {
    companion object {
        fun of(
            email: String,
            password: String,
            name: String,
            nickname: String,
            phone: String,
        ): Customer =
            Customer(
                email = email,
                password = password,
                name = name,
                nickname = nickname,
                phone = phone,
            )
    }
}
