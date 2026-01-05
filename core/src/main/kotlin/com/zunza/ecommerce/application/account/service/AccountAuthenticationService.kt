package com.zunza.ecommerce.application.account.service

import com.zunza.ecommerce.application.account.provided.AccountAuthenticator
import com.zunza.ecommerce.application.account.provided.AccountFinder
import com.zunza.ecommerce.application.account.required.TokenProvider
import com.zunza.ecommerce.application.account.required.TokenRepository
import com.zunza.ecommerce.application.account.service.dto.command.LoginCommand
import com.zunza.ecommerce.application.account.service.dto.command.LogoutCommand
import com.zunza.ecommerce.application.account.service.dto.result.LoginResult
import com.zunza.ecommerce.application.account.service.dto.result.RefreshResult
import com.zunza.ecommerce.domain.account.InvalidCredentialsException
import com.zunza.ecommerce.domain.account.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class AccountAuthenticationService(
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val tokenRepository: TokenRepository,
    private val accountFinder: AccountFinder,
) : AccountAuthenticator {
    override fun login(loginCommand: LoginCommand): LoginResult {
        val account = getAccount(loginCommand)

        account.login(loginCommand.password, passwordEncoder)

        val accessToken = tokenProvider.generateAccessToken(account.id, account.roles.toList())
        val refreshToken = tokenProvider.generateRefreshToken(account.id)

        tokenRepository.save(account.id, refreshToken)

        return LoginResult.Companion.of(account.id, accessToken, refreshToken)
    }

    override fun logout(logoutCommand: LogoutCommand) {
        val remainingTime = tokenProvider.getRemainingTime(logoutCommand.accessToken)

        addBlacklist(logoutCommand.accessToken, remainingTime)

        tokenRepository.removeToken(logoutCommand.accountId)
    }

    override fun refresh(refreshToken: String): RefreshResult {
        tokenProvider.validateToken(refreshToken)

        val accountId = tokenProvider.getAccountId(refreshToken)

        val account = accountFinder.findByIdOrThrow(accountId)

        val newAccessToken = tokenProvider.generateAccessToken(account.id, account.roles.toList())
        val newRefreshToken = tokenProvider.generateRefreshToken(account.id)

        tokenRepository.save(account.id, newRefreshToken)

        return RefreshResult.Companion.of(account.id, newAccessToken, newRefreshToken)
    }

    private fun getAccount(loginCommand: LoginCommand) =
        accountFinder.findByEmail(loginCommand.email) ?: throw InvalidCredentialsException()

    private fun addBlacklist(token: String, remainingTime: Long) {
        if (remainingTime > 0L) {
            tokenRepository.addBlacklist(token, remainingTime)
        }
    }
}