package com.zunza.customer.api.domain.auth.controller

import com.zunza.common.support.resopnse.ApiResponse
import com.zunza.customer.api.domain.auth.dto.request.LoginRequestDto
import com.zunza.customer.api.domain.auth.dto.response.LoginResponseDto
import com.zunza.customer.api.domain.auth.dto.response.RefreshResponseDto
import com.zunza.customer.api.domain.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.CookieValue
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequestDto,
    ): ResponseEntity<ApiResponse<LoginResponseDto>> {
        val result = authService.login(request)
        val loginResponseDto = LoginResponseDto.from(result)
        val cookie = generateRefreshTokenCookie(result.refreshToken, 7L)

        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.success(loginResponseDto))
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal customerId: Long,
    ): ResponseEntity<ApiResponse<Any>> {
        authService.logout(customerId)
        val cookie = generateRefreshTokenCookie("", 0L)
        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.success())
    }

    @PostMapping("/token/refresh")
    fun refresh(
        @RequestHeader("Authorization") bearerToken: String,
        @CookieValue("refreshToken") refreshToken: String,
    ): ResponseEntity<ApiResponse<RefreshResponseDto>> {
        val result = authService.tokenRefresh(bearerToken, refreshToken)
        val refreshResponseDto = RefreshResponseDto.from(result.newAccessToken)
        val cookie = generateRefreshTokenCookie(result.newRefreshToken, 7L)

        return ResponseEntity
            .ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.success(refreshResponseDto))
    }

    private fun generateRefreshTokenCookie(
        value: String,
        maxAge: Long,
    ) = ResponseCookie
        .from("refreshToken", value)
        .httpOnly(true)
        .path("/")
        .maxAge(Duration.ofDays(maxAge))
        .build()
}
