package com.zunza.ecommerce.application.auth.provided

import com.zunza.ecommerce.application.auth.service.dto.result.RefreshResult

interface RefreshUseCase {
    fun refresh(refreshToken: String): RefreshResult
}