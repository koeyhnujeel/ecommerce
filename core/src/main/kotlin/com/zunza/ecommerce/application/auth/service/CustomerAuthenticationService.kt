package com.zunza.ecommerce.application.auth.service

import com.zunza.ecommerce.application.account.provided.GetCustomerAccountUseCase
import com.zunza.ecommerce.application.auth.provided.LoginUseCase
import com.zunza.ecommerce.application.auth.provided.LogoutUseCase
import com.zunza.ecommerce.application.auth.provided.RefreshUseCase
import com.zunza.ecommerce.application.auth.required.TokenProvider
import com.zunza.ecommerce.application.auth.required.TokenRepository
import com.zunza.ecommerce.application.auth.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.auth.service.dto.command.LogoutCommand
import com.zunza.ecommerce.application.auth.service.dto.result.LoginResult
import com.zunza.ecommerce.application.auth.service.dto.result.RefreshResult
import com.zunza.ecommerce.domain.account.InvalidCredentialsException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CustomerAuthenticationService(
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val tokenRepository: TokenRepository,
    private val getCustomerAccountUseCase: GetCustomerAccountUseCase,
) : LoginUseCase,
    LogoutUseCase,
    RefreshUseCase {
    override fun login(loginCommand: LoginCommand): LoginResult {
        val account = getAccount(loginCommand)

        account.login(loginCommand.password, passwordEncoder)

        val accessToken = tokenProvider.generateAccessToken(account.id, account.role.toString())
        val refreshToken = tokenProvider.generateRefreshToken(account.id)

        tokenRepository.save(account.id, refreshToken)

        return LoginResult.of(account.id, accessToken, refreshToken)
    }

    override fun logout(logoutCommand: LogoutCommand) {
        val remainingTime = tokenProvider.getRemainingTime(logoutCommand.accessToken)

        addBlacklist(logoutCommand.accessToken, remainingTime)

        tokenRepository.removeToken(logoutCommand.accountId)
    }

    override fun refresh(refreshToken: String): RefreshResult {
        tokenProvider.validateToken(refreshToken)

        val accountId = tokenProvider.getAccountId(refreshToken)

        val account = getCustomerAccountUseCase.findByIdOrThrow(accountId)

        val newAccessToken = tokenProvider.generateAccessToken(account.id, account.role.toString())
        val newRefreshToken = tokenProvider.generateRefreshToken(account.id)

        tokenRepository.save(account.id, newRefreshToken)

        return RefreshResult.of(account.id, newAccessToken, newRefreshToken)
    }

    private fun getAccount(loginCommand: LoginCommand) =
        getCustomerAccountUseCase.findByEmail(loginCommand.email) ?: throw InvalidCredentialsException()

    private fun addBlacklist(token: String, remainingTime: Long) {
        if (remainingTime > 0L) {
            tokenRepository.addBlacklist(token, remainingTime)
        }
    }
}