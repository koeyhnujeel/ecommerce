package com.zunza.ecommerce.dto.request

import com.zunza.ecommerce.dto.command.LoginCommand
import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "이메일을 입력해 주세요.")
    val email: String,
    @field:NotBlank(message = "비밀번호를 입력해 주세요.")
    val password: String,
) {
    fun toCommand() = LoginCommand(
        this.email,
        this.password
    )
}
