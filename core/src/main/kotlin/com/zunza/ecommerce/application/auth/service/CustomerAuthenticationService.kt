package com.zunza.ecommerce.application.auth.service

import com.zunza.ecommerce.application.account.provided.AccountFinder
import com.zunza.ecommerce.application.auth.provided.CustomerAuthentication
import com.zunza.ecommerce.application.auth.required.TokenProvider
import com.zunza.ecommerce.application.auth.required.TokenRepository
import com.zunza.ecommerce.application.auth.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.auth.service.dto.result.LoginResult
import com.zunza.ecommerce.domain.account.InvalidCredentialsException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class CustomerAuthenticationService(
    private val tokenProvider: TokenProvider,
    private val accountFinder: AccountFinder,
    private val passwordEncoder: PasswordEncoder,
    private val tokenRepository: TokenRepository
) : CustomerAuthentication {
    override fun login(loginCommand: LoginCommand): LoginResult {
        val account = findAccount(loginCommand)

        account.login(loginCommand.password, passwordEncoder)

        val accessToken = tokenProvider.generateAccessToken(account.id, account.role.toString())
        val refreshToken = tokenProvider.generateRefreshToken(account.id)

        tokenRepository.save(account.id, refreshToken)

        return LoginResult.of(account.id, accessToken, refreshToken)
    }

    private fun findAccount(loginCommand: LoginCommand) =
        accountFinder.findByEmail(loginCommand.email) ?: throw InvalidCredentialsException()
}