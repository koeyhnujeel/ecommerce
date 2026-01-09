package com.zunza.ecommerce.domain.account

import com.zunza.ecommerce.domain.AbstractEntity
import com.zunza.ecommerce.domain.shared.Email
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
class Account private constructor(
    val email: Email,
    var passwordHash: String,
    var status: AccountStatus,
    val roles: MutableSet<UserRole>,
    val registeredAt: LocalDateTime,
    var activatedAt: LocalDateTime?,
    var lastLoginAt: LocalDateTime?,
    var deactivatedAt: LocalDateTime?
) : AbstractEntity() {
    companion object {
        fun register(
            email: String,
            password: String,
            passwordEncoder: PasswordEncoder,
        ): Account {
            require(PasswordValidator.isValid(password)) { "잘못된 비밀번호 형식입니다." }

            return Account(
                email = Email(email),
                passwordHash = passwordEncoder.encode(password),
                status = AccountStatus.PENDING,
                roles = mutableSetOf(UserRole.ROLE_CUSTOMER),
                registeredAt = LocalDateTime.now(),
                activatedAt = null,
                lastLoginAt = null,
                deactivatedAt = null,
            )
        }
    }

    fun activate() {
        check(this.status == AccountStatus.PENDING) { "PENDING 상태가 아닙니다." }

        this.status = AccountStatus.ACTIVE
        this.activatedAt = LocalDateTime.now()
    }

    fun deactivate() {
        check(this.status == AccountStatus.ACTIVE) { "ACTIVE 상태가 아닙니다." }

        this.status = AccountStatus.DEACTIVATED
        this.deactivatedAt = LocalDateTime.now()
    }

    fun changePassword(password: String, passwordEncoder: PasswordEncoder) {
        check(this.status == AccountStatus.ACTIVE) { "ACTIVE 상태가 아닙니다." }
        require(PasswordValidator.isValid(password)) { "잘못된 비밀번호 형식입니다." }

        this.passwordHash = passwordEncoder.encode(password)
    }

    fun login(password: String, passwordEncoder: PasswordEncoder) {
        check(this.status == AccountStatus.ACTIVE) { "ACTIVE 상태가 아닙니다." }

        if (!passwordEncoder.matches(password, this.passwordHash)) {
            throw InvalidCredentialsException()
        }

        this.lastLoginAt = LocalDateTime.now()
    }

    fun grantPartnerRole() {
        check(this.status == AccountStatus.ACTIVE) { "ACTIVE 상태가 아닙니다." }

        this.roles.add(UserRole.ROLE_SELLER)
    }
}