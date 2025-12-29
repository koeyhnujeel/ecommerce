package com.zunza.ecommerce.adapter.webapi.auth

import com.zunza.ecommerce.adapter.ApiResponse
import com.zunza.ecommerce.adapter.security.jwt.JwtProperties
import com.zunza.ecommerce.adapter.webapi.auth.dto.request.LoginRequest
import com.zunza.ecommerce.adapter.webapi.auth.dto.response.LoginResponse
import com.zunza.ecommerce.adapter.webapi.auth.dto.response.RefreshResponse
import com.zunza.ecommerce.application.auth.provided.LoginUseCase
import com.zunza.ecommerce.application.auth.provided.LogoutUseCase
import com.zunza.ecommerce.application.auth.provided.RefreshUseCase
import com.zunza.ecommerce.application.auth.service.dto.command.LogoutCommand
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class AuthApi(
    private val jwtProperties: JwtProperties,
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val refreshUseCase: RefreshUseCase,
) {
    @PostMapping("/login")
    fun login(
        @RequestBody @Valid request: LoginRequest
    ): ResponseEntity<ApiResponse<LoginResponse>> {
        val result = loginUseCase.login(request.toCommand())

        val accessTokenCookie = getAccessTokenCookie(result.accessToken)
        val refreshTokenCookie = getRefreshTokenCookie(result.refreshToken)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(ApiResponse.success(LoginResponse.from(result.accountId)))
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal accountId: Long,
        @CookieValue(value = "accessToken") accessToken: String,
    ): ResponseEntity<ApiResponse<Any>> {
        logoutUseCase.logout(LogoutCommand.of(accountId, accessToken))

        val accessTokenCookie = getAccessTokenCookie("", Duration.ZERO)
        val refreshTokenCookie = getRefreshTokenCookie("", Duration.ZERO)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(ApiResponse.success())
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(value = "refreshToken") refreshToken: String,
    ): ResponseEntity<ApiResponse<RefreshResponse>> {
        val result = refreshUseCase.refresh(refreshToken)

        val accessTokenCookie = getAccessTokenCookie(result.accessToken)
        val refreshTokenCookie = getRefreshTokenCookie(result.refreshToken)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
            .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
            .body(ApiResponse.success(RefreshResponse.from(result.accountId)))
    }

    private fun getAccessTokenCookie(
        value: String,
        maxAge: Duration = Duration.ofMillis(jwtProperties.accessTokenTtl)
    ) = createResponseCookie("accessToken", value, maxAge, "Lax")

    private fun getRefreshTokenCookie(
        value: String,
        maxAge: Duration = Duration.ofMillis(jwtProperties.refreshTokenTtl)
    ) = createResponseCookie("refreshToken", value, maxAge, "Strict")

    private fun createResponseCookie(
        name: String,
        value: String,
        maxAge: Duration,
        sameSite: String
    ) = ResponseCookie.from(name, value)
        .httpOnly(true)
        .path("/")
        .maxAge(maxAge)
        .sameSite(sameSite)
        .build()
}