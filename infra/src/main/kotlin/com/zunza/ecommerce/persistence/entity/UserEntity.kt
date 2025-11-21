package com.zunza.ecommerce.persistence.entity

import com.zunza.ecommerce.domain.enums.UserType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class UserEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false)
    val password: String,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val userType: UserType
) : BaseTimeEntity()
