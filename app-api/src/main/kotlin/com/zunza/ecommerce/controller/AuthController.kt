package com.zunza.ecommerce.controller

import com.zunza.ecommerce.dto.request.LoginRequest
import com.zunza.ecommerce.dto.request.SignupRequest
import com.zunza.ecommerce.dto.response.LoginResponse
import com.zunza.ecommerce.service.AuthService
import com.zunza.ecommerce.support.resopnse.ApiResponse
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService,
) {
    @GetMapping("/email/validation")
    fun checkEmailDuplicate(
        @RequestParam email: String
    ): ApiResponse<Any> {
        authService.validateEmailAvailable(email)
        return ApiResponse.success()
    }

    @GetMapping("/phone/validation")
    fun checkPhoneDuplicate(
        @RequestParam phone: String
    ): ApiResponse<Any> {
        authService.validatePhoneAvailable(phone)
        return ApiResponse.success()
    }

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.CREATED)
    fun signupCustomer(
        @Valid @RequestBody request: SignupRequest,
    ): ApiResponse<Any> {
        authService.createCustomer(request.toCommand())
        return ApiResponse.success()
    }

    @PostMapping("/login")
    fun login(
        @Valid @RequestBody request: LoginRequest
    ): ResponseEntity<ApiResponse<LoginResponse>> {
        val loginResult = authService.authenticate(request.toCommand())
        val cookie = generateRefreshTokenCookie(loginResult.refreshToken, 7L)
        val loginResponse = LoginResponse.of(loginResult.accessToken)

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.success(loginResponse))
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
